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
import com.eustrosoft.core.model.ranges.MSGPartyRole;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import static com.eustrosoft.core.constants.DBConstants.ZOID;
import static com.eustrosoft.core.constants.DBConstants.ZRID;

public final class MSGDao extends BasicDAO {

    public MSGDao(QDBPConnection poolConnection) {
        super(poolConnection);
    }

    @SneakyThrows
    public MSGChannel getChat(Long zoid) {
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

    @SneakyThrows
    public ResultSet getChats() {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("MSG.V_CChannel")
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    @SneakyThrows
    public ExecStatus createChat(MSGChannel channel, Integer slvl) {
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
        return status;
    }

    @SneakyThrows
    public MSGMessage getMessage(Long zoid, Long zrid) {
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

    @SneakyThrows
    public ResultSet getMessages(Long channelId) {
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

    @SneakyThrows
    public ExecStatus createMessage(Long chatId, MSGMessage message) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return null;
        }
        ExecStatus openedChat = openObject("MSG.C", chatId);
        try {
            if (openedChat.isOk()) {
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

                ExecStatus status = new ExecStatus();
                if (preparedStatement != null) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    status.fillFromResultSet(resultSet);
                    preparedStatement.close();
                    resultSet.close();
                }
                return status;
            }
            return new ExecStatus();
        } finally {
            commitObject("MSG.C", openedChat.getZoid(), openedChat.getZver());
        }
    }

    @SneakyThrows
    private ExecStatus createCParty(String chatId, String chatVer, MSGParty party) {
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
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public void updateMessage(MSGMessage message) {
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
                ExecStatus updatedStatus = new ExecStatus();
                if (preparedStatement != null) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    updatedStatus.fillFromResultSet(resultSet);
                    preparedStatement.close();
                    resultSet.close();
                }
            }
        } finally {
            if (status != null) {
                commitObject("MSG.C", status.getZoid(), status.getZver());
            }
        }
    }

    @SneakyThrows
    public void updateChannel(MSGChannel channel) {
        ExecStatus status = null;
        try {
            if (channel == null) {
                throw new Exception("CChannel is null while updating.");
            }
            status = openObject("MSG.C", channel.getZoid());
            channel.setZver(status.getZver());
            if (status.isOk()) {
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
                ExecStatus updatedStatus = new ExecStatus();
                if (preparedStatement != null) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    updatedStatus.fillFromResultSet(resultSet);
                    preparedStatement.close();
                    resultSet.close();
                }
                if (!updatedStatus.isOk()) {
                    throw new Exception("Update failed.");
                }
            }
        } finally {
            if (status != null) {
                commitObject("MSG.C", status.getZoid(), status.getZver());
            }
        }
    }

    @SneakyThrows
    public ExecStatus deleteMessage(Long zoid, Long zrid) {
        Connection connection = getPoolConnection().get();
        ExecStatus openedObject = openObject("MSG.C", zoid);
        ExecStatus status = new ExecStatus();
        try {
            if (!openedObject.isOk()) {
                throw new Exception("Problem while delete message");
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
            if (preparedStatement != null) {
                ResultSet resultSet = preparedStatement.executeQuery();
                status.fillFromResultSet(resultSet);
                preparedStatement.close();
                resultSet.close();
                if (!status.isOk()) {
                    throw new Exception("Problem while delete message");
                }
            }
        } finally {
            commitObject("MSG.C", openedObject.getZoid(), openedObject.getZver());
        }
        return status;
    }
}
