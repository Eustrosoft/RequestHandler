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

public final class MSGDao extends BasicDAO {

    public MSGDao(QDBPConnection poolConnection) {
        super(poolConnection);
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
    public ResultSet getMessages(Long channelId) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .leftBracket()
                        .add("cm.zoid, cm.zver, cm.zrid, cm.zlvl, cm.content, cm.msg_id, cm.type, tz.zuid")
                        .rightBracket()
                        .from()
                        .add("MSG.v_cchannel as cc")
                        .add("right outer join")
                        .add("MSG.v_cmsg as cm")
                        .on()
                        .leftBracket()
                        .add("cc.zoid = cm.zoid")
                        .rightBracket()
                        .add("left outer join tis.vh_zobject as tz")
                        .on()
                        .leftBracket()
                        .add("cm.zver = tz.zver")
                        .rightBracket()
                        .where(
                                Query.builder()
                                        .add(channelId)
                                        .eq()
                                        .add("tz.zoid")
                                        .and()
                                        .add("cc.zoid")
                                        .eq()
                                        .add(channelId)
                                        .build()
                        )
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
        ExecStatus openedChat = openObject("MSG.C", chatId);
        try {
            if (openedChat.isOk()) {
                Connection connection = getPoolConnection().get();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        Query.builder()
                                .select()
                                .add("MSG.Create_cmsg")
                                .leftBracket()
                                .add(String.format(
                                        //v_zoid bigint, v_zver bigint, v_zpid bigint, v_content character varying, v_msg_id bigint, v_type character varying
                                        "%s, %s, %s, '%s', %s, '%s'",
                                        openedChat.getZoid(),
                                        openedChat.getZver(),
                                        1, // todo
                                        message.getContent(),
                                        message.getAnswerId(),
                                        message.getType().getValue()
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
