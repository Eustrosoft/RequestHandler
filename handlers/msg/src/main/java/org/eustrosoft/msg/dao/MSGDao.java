/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg.dao;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.db.ExecStatus;
import org.eustrosoft.core.db.dao.BasicDAO;
import org.eustrosoft.core.db.util.DBUtils;
import org.eustrosoft.handlers.msg.dto.base.MSGChannelStatus;
import org.eustrosoft.handlers.msg.dto.base.MSGPartyRole;
import org.eustrosoft.msg.model.MSGChannel;
import org.eustrosoft.msg.model.MSGMessage;
import org.eustrosoft.msg.model.MSGParty;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.sam.dao.SamDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public final class MSGDao extends BasicDAO {

    public MSGDao(QDBPConnection poolConnection) {
        super(poolConnection);
    }

    public MSGChannel getChat(Long zoid) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM MSG.V_CChannel WHERE ZOID = ?"
        );
        DBUtils.setLongOrNull(preparedStatement, 1, zoid);
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
        String condition = MSGChannelStatus.toSQLWhere(String.format("%s.%s", "msg", DBConstants.STATUS), statuses);
        StringBuilder queryBuilder = new StringBuilder(
                "select ts.zoid, ts.zver, msg.zrid, msg.zsid, msg.zlvl, msg.zpid, msg.subject, msg.status, msg.obj_id " +
                        "from msg.v_cchannel as msg " +
                        "left join tis.v_zobject as ts " +
                        "on msg.zoid = ts.zoid"
        );
        if (condition != null && !condition.isEmpty()) {
            queryBuilder.append(" WHERE ").append(condition);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(
                queryBuilder.toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    public ResultSet getChatsVersions(List<MSGChannelStatus> statuses) throws SQLException {
        Connection connection = getPoolConnection().get();
        String condition = MSGChannelStatus.toSQLWhere(String.format("%s.%s", "msg", DBConstants.STATUS), statuses);
        StringBuilder queryBuilder = new StringBuilder(
                "select ts.zoid, ts.zver, msg.status from msg.v_cchannel as msg left join " +
                        "tis.v_zobject as ts on msg.zoid = ts.zoid where ts.ztype = 'MSG.C'"
        );
        if (condition != null && !condition.isEmpty()) {
            queryBuilder.append(" WHERE ").append(condition);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(
                queryBuilder.toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    public ExecStatus createChat(MSGChannel channel) throws Exception {
        SamDAO samDAO = new SamDAO(getPoolConnection());
        Long zsid = channel.getZSID() == null ? samDAO.getUserSid() : channel.getZSID();
        ExecStatus objectInScope = createObjectInScope("MSG.C", zsid, channel.getZLVL());
        if (!objectInScope.isOk()) {
            throw new Exception(objectInScope.getCaption());
        }
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT MSG.create_CChannel(?, ?, ?, ?, ?, ?)"
        );
        DBUtils.setLongOrNull(preparedStatement, 1, objectInScope.getZoid());
        DBUtils.setLongOrNull(preparedStatement, 2, objectInScope.getZver());
        preparedStatement.setNull(3, Types.BIGINT);
        DBUtils.setStringOrNull(preparedStatement, 4, channel.getSubject());
        DBUtils.setStringOrNull(preparedStatement, 5, channel.getStatus().getValue());
        DBUtils.setLongOrNull(preparedStatement, 6, channel.getDocumentId());
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        if (status.isOk()) {
            createCParty(
                    objectInScope.getZoid(), status.getZver(),
                    new MSGParty(samDAO.getUserId(), MSGPartyRole.C, null)
            );
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
                "SELECT * FROM MSG.V_CMsg where ZOID = ? AND ZRID = ?"
        );
        DBUtils.setLongOrNull(preparedStatement, 1, zoid);
        DBUtils.setLongOrNull(preparedStatement, 2, zrid);
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
                "SELECT CM.zoid, CM.zver, CM.zrid, CM.zlvl, CM.content, CM.msg_id, CM.type, ZO.zuid, ZO.zdate, XU.login, XU.full_name, ZO.qrsq > CM.zrid " +
                        "FROM msg.V_CMsg as CM LEFT OUTER JOIN TIS.VH_ZObject as ZO " +
                        "ON (CM.zoid = ZO.zoid AND CM.zver = ZO.zver) LEFT OUTER JOIN SAM.V_User as XU " +
                        "ON (XU.id = ZO.zuid) WHERE (CM.zoid = ?) ORDER BY CM.zrid"
        );
        DBUtils.setLongOrNull(preparedStatement, 1, channelId);
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
                    "SELECT MSG.create_CMsg(?, ?, ?, ?, ?, ?)"
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

    private ExecStatus createCParty(Long chatId, Long chatVer, MSGParty party) throws Exception {
        SamDAO samDAO = new SamDAO(getPoolConnection());
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT MSG.create_CParty(?, ?, ?, ?, ?, ?)"
        );
        DBUtils.setLongOrNull(preparedStatement, 1, chatId);
        DBUtils.setLongOrNull(preparedStatement, 2, chatVer);
        DBUtils.setLongOrNull(preparedStatement, 3, 1L);
        DBUtils.setLongOrNull(preparedStatement, 4, samDAO.getUserId());
        DBUtils.setStringOrNull(preparedStatement, 5, party.getRole().getValue());
        preparedStatement.setNull(6, Types.BIGINT);
        return execute(preparedStatement);
    }

    public void updateMessage(MSGMessage message) throws Exception {
        ExecStatus status = null;
        try {
            if (message == null) {
                throw new Exception("CMessage is null while updating.");
            }
            status = openObject("MSG.C", message.getZOID());
            message.setZVER(status.getZver());
            if (status.isOk()) {
                Connection connection = getPoolConnection().get();
                MSGMessage oldMessage = getMessage(message.getZOID(), message.getZRID());
                message.merge(oldMessage); // todo: make it another way
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT MSG.update_CMsg(?, ?, ?, ?, ?, ?)"
                );
                DBUtils.setLongOrNull(preparedStatement, 1, message.getZOID());
                DBUtils.setLongOrNull(preparedStatement, 2, message.getZVER());
                DBUtils.setLongOrNull(preparedStatement, 3, message.getZRID());
                DBUtils.setStringOrNull(preparedStatement, 4, message.getContent());
                DBUtils.setLongOrNull(preparedStatement, 5, message.getAnswerId());
                DBUtils.setStringOrNull(preparedStatement, 6, message.getType().getValue());
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
            status = openObject("MSG.C", channel.getZOID());
            channel.setZVER(status.getZver());
            if (!status.isOk()) {
                throw new Exception(status.getCaption());
            }
            MSGChannel oldChannel = getChat(channel.getZOID());
            channel.merge(oldChannel);
            Connection connection = getPoolConnection().get();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT MSG.update_CChannel(?, ?, ?, ?, ?, ?)"
            );
            DBUtils.setLongOrNull(preparedStatement, 1, channel.getZOID());
            DBUtils.setLongOrNull(preparedStatement, 2, channel.getZVER());
            DBUtils.setLongOrNull(preparedStatement, 3, channel.getZRID());
            DBUtils.setStringOrNull(preparedStatement, 4, channel.getSubject());
            DBUtils.setStringOrNull(preparedStatement, 5, channel.getStatus().getValue());
            DBUtils.setLongOrNull(preparedStatement, 6, channel.getDocumentId());
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
                    "SELECT MSG.delete_CMsg(?, ?, ?)"
            );
            DBUtils.setLongOrNull(preparedStatement, 1, openedObject.getZoid());
            DBUtils.setLongOrNull(preparedStatement, 2, openedObject.getZver());
            DBUtils.setLongOrNull(preparedStatement, 3, zrid);
            status = execute(preparedStatement);
        } finally {
            commitObject("MSG.C", openedObject.getZoid(), openedObject.getZver());
        }
        return status;
    }

    public ExecStatus deleteChannel(Long zoid, Long zver) throws Exception {
        return deleteObject("MSG.C", zoid, null);
    }
}
