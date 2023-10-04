/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.db.dao;

import com.eustrosoft.core.db.ExecStatus;
import com.eustrosoft.core.db.Query;
import com.eustrosoft.core.model.MSGChannel;
import com.eustrosoft.core.model.MSGMessage;
import com.eustrosoft.core.model.MSGParty;
import com.eustrosoft.core.model.ranges.MSGChannelStatus;
import com.eustrosoft.core.model.ranges.MSGPartyRole;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static com.eustrosoft.core.constants.DBConstants.STATUS;
import static com.eustrosoft.core.constants.DBConstants.ZOID;
import static com.eustrosoft.core.constants.DBConstants.ZRID;

public final class MSGDao extends BasicDAO {

    public MSGDao(QDBPConnection poolConnection) {
        super(poolConnection);
    }

    public MSGChannel getChat(Long zoid) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("MSG.V_CChannel")
                        .where(String.format("%s = %d", ZOID, zoid))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            MSGChannel channel = new MSGChannel();
            if (resultSet.next()) {
                channel.fillFromResultSet(resultSet);
            }
            preparedStatement.close();
            resultSet.close();
            return channel;
        }
        return null;
    }

    public ResultSet getChats(List<MSGChannelStatus> statuses) throws SQLException {
        Connection connection = getPoolConnection().get();
        String condition = MSGChannelStatus.toSQLWhere(STATUS, statuses);
        Query.Builder query = Query.builder()
                .select()
                .all()
                .from()
                .add("MSG.V_CChannel");
        if (condition != null && !condition.isEmpty()) {
            query.where(condition);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(
                query.buildWithSemicolon().toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    public ExecStatus createChat(MSGChannel channel, Integer slvl) throws Exception {
        // ??? todo zsid
        SamDAO samDAO = new SamDAO(getPoolConnection());
        ExecStatus objectInScope = createObjectInScope("MSG.C", samDAO.getUserSid(), slvl == null ? "null" : slvl.toString());
        if (!objectInScope.isOk()) {
            throw new Exception(objectInScope.getCaption());
        }
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("MSG.create_cchannel")
                        .leftBracket()
                        .add(String.format(
                                //v_zoid bigint, v_zver bigint, v_zpid bigint, v_subject character varying, v_status character, v_obj_id bigint
                                "%s, %s, %s, '%s', '%s', %s",
                                objectInScope.getZoid(),
                                objectInScope.getZver(),
                                null,
                                channel.getSubject(),
                                channel.getStatus().getValue(),
                                channel.getDocumentId()
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        // todo: create prep statement with ?
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        if (status.isOk()) {
            createCParty(
                    objectInScope.getZoid().toString(),
                    status.getZver().toString(),
                    new MSGParty(samDAO.getUserId(), MSGPartyRole.C, null)
            ); // todo: maybe create a message for creator
        }
        commitObject(
                "MSG.C",
                objectInScope.getZoid(),
                status.getZver()
        );
        return objectInScope;
    }

    public MSGMessage getMessage(Long zoid, Long zrid) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("MSG.V_CMsg")
                        .where(String.format("%s = %d AND %s = %d", ZOID, zoid, ZRID, zrid))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            MSGMessage message = new MSGMessage();
            if (resultSet.next()) {
                message.fillFromResultSet(resultSet);
            }
            preparedStatement.close();
            resultSet.close();
            return message;
        }
        return null;
    }

    public ResultSet getMessages(Long channelId) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("cm.zoid, cm.zver, cm.zrid, cm.zlvl, cm.content, cm.msg_id, cm.type, " +
                                "zo.zuid, zo.zdate, xu.login, xu.full_name, zo.qrsq > cm.zrid")
                        .from()
                        .add("msg.v_cmsg as cm")
                        .add("left outer join")
                        .add("tis.vh_zobject as zo")
                        .on()
                        .leftBracket()
                        .add("cm.zoid = zo.zoid and cm.zver = zo.zver")
                        .rightBracket()
                        .add("left outer join sam.V_User as xu")
                        .on()
                        .leftBracket()
                        .add("xu.id = zo.zuid")
                        .rightBracket()
                        .where(
                                Query.builder()
                                        .add("cm.zoid")
                                        .eq()
                                        .add(channelId)
                                        .build()
                        )
                        .add("order by cm.zrid")
                        .buildWithSemicolon()
                        .getQuery().toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    public ExecStatus createMessage(Long chatId, MSGMessage message) throws Exception {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return null;
        }
        ExecStatus openedChat = openObject("MSG.C", chatId);
        try {
            if (!openedChat.isOk()) {
                throw new Exception(openedChat.getCaption());
            }
            Connection connection = getPoolConnection().get();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    Query.builder()
                            .select()
                            .add("MSG.Create_cmsg")
                            .leftBracket()
                            .add("?, ?, ?, ?, ?, ?")
                            .rightBracket()
                            .buildWithSemicolon()
                            .toString()
            );
            preparedStatement.setLong(1, openedChat.getZoid());
            preparedStatement.setLong(2, openedChat.getZver());
            preparedStatement.setLong(3, 1); // todo
            preparedStatement.setString(4, message.getContent());
            if (message.getAnswerId() != null) {
                preparedStatement.setLong(5, message.getAnswerId());
            } else {
                preparedStatement.setNull(5, Types.BIGINT);
            }
            if (message.getType() != null) {
                preparedStatement.setString(6, message.getType().getValue());
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            return execute(preparedStatement);
        } finally {
            commitObject("MSG.C", openedChat.getZoid(), openedChat.getZver());
        }
    }

    private ExecStatus createCParty(String chatId, String chatVer, MSGParty party) throws Exception {
        SamDAO samDAO = new SamDAO(getPoolConnection());
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("MSG.create_cparty")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, %s, '%s', %s",
                                chatId,
                                chatVer,
                                1, // todo
                                samDAO.getUserId(),
                                party.getRole().getValue(),
                                null
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        return execute(preparedStatement);
    }

    public void updateMessage(MSGMessage message) throws Exception {
        ExecStatus status = null;
        try {
            if (message == null) {
                throw new Exception("CMessage is null while updating.");
            }
            status = openObject("MSG.C", message.getZoid());
            message.setZver(status.getZver());
            if (status.isOk()) {
                Connection connection = getPoolConnection().get();
                MSGMessage oldMessage = getMessage(message.getZoid(), message.getZrid());
                message.merge(oldMessage); // todo: make it another way
                PreparedStatement preparedStatement = connection.prepareStatement(
                        Query.builder()
                                .select()
                                .add("MSG.update_CMsg")
                                .leftBracket()
                                .add(message.toUpdateString())
                                .rightBracket()
                                .buildWithSemicolon()
                                .toString()
                );
                execute(preparedStatement);
            }
        } finally {
            if (status != null) {
                commitObject("MSG.C", status.getZoid(), status.getZver());
            }
        }
    }

    public void updateChannel(MSGChannel channel) throws Exception {
        ExecStatus status = null;
        try {
            if (channel == null) {
                throw new Exception("CChannel is null while updating.");
            }
            status = openObject("MSG.C", channel.getZoid());
            channel.setZver(status.getZver());
            if (!status.isOk()) {
                throw new Exception(status.getCaption());
            }
            MSGChannel oldChannel = getChat(channel.getZoid());
            channel.merge(oldChannel); // todo: make it another way
            Connection connection = getPoolConnection().get();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    Query.builder()
                            .select()
                            .add("MSG.update_CChannel")
                            .leftBracket()
                            .add(channel.toUpdateString())
                            .rightBracket()
                            .buildWithSemicolon()
                            .toString()
            );
            execute(preparedStatement);
        } finally {
            if (status != null) {
                commitObject("MSG.C", status.getZoid(), status.getZver());
            }
        }
    }

    public ExecStatus deleteMessage(Long zoid, Long zrid) throws Exception {
        Connection connection = getPoolConnection().get();
        ExecStatus openedObject = openObject("MSG.C", zoid);
        ExecStatus status = new ExecStatus();
        try {
            if (!openedObject.isOk()) {
                throw new Exception(openedObject.getCaption());
            }
            PreparedStatement preparedStatement = connection.prepareStatement(
                    Query.builder()
                            .select()
                            .add("MSG.delete_CMsg")
                            .leftBracket()
                            .add(String.format(
                                    "%s, %s, %s",
                                    openedObject.getZoid(), openedObject.getZver(), zrid
                            ))
                            .rightBracket()
                            .buildWithSemicolon()
                            .toString()
            );
            status = execute(preparedStatement);
        } finally {
            commitObject("MSG.C", openedObject.getZoid(), openedObject.getZver());
        }
        return status;
    }

    public ExecStatus deleteChannel(Long zoid) throws Exception {
        Connection connection = getPoolConnection().get();
        ExecStatus openedObject = openObject("MSG.C", zoid);
        ExecStatus status = null;
        try {
            if (!openedObject.isOk()) {
                throw new Exception(openedObject.getCaption());
            }
            PreparedStatement preparedStatement = connection.prepareStatement(
                    Query.builder()
                            .select()
                            .add("MSG.delete_CChannel")
                            .leftBracket()
                            .add(String.format(
                                    "%s, %s, null",
                                    openedObject.getZoid(), openedObject.getZver()
                            ))
                            .rightBracket()
                            .buildWithSemicolon()
                            .toString()
            );
            status = execute(preparedStatement);
        } finally {
            commitObject("MSG.C", openedObject.getZoid(), openedObject.getZver());
        }
        return status;
    }
}
