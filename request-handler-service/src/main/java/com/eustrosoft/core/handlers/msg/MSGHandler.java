/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.msg;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.datasource.sources.model.MSGChannel;
import com.eustrosoft.datasource.sources.model.MSGMessage;
import com.eustrosoft.datasource.sources.ranges.MSGChannelStatus;
import com.eustrosoft.datasource.sources.ranges.MSGMessageType;
import com.eustrosoft.dbdatasource.core.DBFunctions;
import com.eustrosoft.dbdatasource.core.DBStatements;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.Constants.*;
import static com.eustrosoft.dbdatasource.constants.DBConstants.*;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.*;

public final class MSGHandler implements Handler {
    private String requestType;
    private UsersContext usersContext;
    private QDBPConnection poolConnection;

    public MSGHandler(String requestType) {
        this.requestType = requestType;
        this.usersContext = UsersContext.getInstance();
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws SQLException {
        QDBPSession session = new SessionProvider(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                .getSession();
        this.poolConnection = session.getConnection();
        MSGRequestBlock msgRequestBlock = (MSGRequestBlock) requestBlock;
        MsgParams params = msgRequestBlock.getParams();
        MSGResponseBlock msgResponseBlock = new MSGResponseBlock();
        msgResponseBlock.setE(0);
        msgResponseBlock.setErrMsg("Ok.");
        // TODO
        switch (requestType) {
            case REQUEST_CHATS:
                List<MSGChannel> chats = getChats();
                break;
            // other requests process
            case REQUEST_CHAT:
                getChatMessages(params.getId());
                break;
            case REQUEST_CREATE:
                createChat(msgRequestBlock.getId());
                break;
            case REQUEST_SEND:
                createMessage(params);
                break;
            case REQUEST_EDIT:
                updateMessage(params);
                break;
            case REQUEST_DELETE:
                deleteMessage(msgRequestBlock.getId());
                break;
            case REQUEST_CHANGE:
                changeChannelStatus(params);
                break;
            default:
                msgResponseBlock.setE(1);
                msgResponseBlock.setErrMsg("Has no this request type");
                break;
        }
        return msgResponseBlock;
    }

    public List<MSGChannel> getChats() throws SQLException {
        Connection connection = poolConnection.get();
        // get user id
        User sqlUser = UsersContext.getInstance().getSQLUser(poolConnection.getSessionID().toString());
        PreparedStatement preparedStatement = DBStatements.getChats(connection, null);
        List<MSGChannel> channels = new ArrayList<>();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            channels = processResultSetToMSGChannels(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return channels;
    }

    public List<MSGMessage> getChatMessages(String chatId) throws SQLException {
        DBFunctions functions = new DBFunctions(poolConnection);
//        PreparedStatement preparedStatement = functions.getMessages("id", null);
//        List<MSGMessage> channels = new ArrayList<>();
//        if (preparedStatement != null) {
//            ResultSet resultSet = preparedStatement.executeQuery();
//            channels = processResultSetToMSGMessage(resultSet);
//            preparedStatement.close();
//            resultSet.close();
//        }
        return null;
    }

    public String createChat(String objId) {
        DBFunctions functions = new DBFunctions(poolConnection);
        return "id";
    }

    public String createMessage(MsgParams params) {
        DBFunctions functions = new DBFunctions(poolConnection);
        return "id";
    }

    public void updateMessage(MsgParams params) {
        DBFunctions functions = new DBFunctions(poolConnection);
    }

    public void deleteMessage(String messageId) {
        DBFunctions functions = new DBFunctions(poolConnection);
    }

    public void changeChannelStatus(MsgParams params) {
        DBFunctions functions = new DBFunctions(poolConnection);
    }

    @SneakyThrows
    private List<MSGMessage> processResultSetToMSGMessage(ResultSet resultSet) {
        List<MSGMessage> objects = new ArrayList<>();
        try {
            while (resultSet.next()) {
                try {
                    String content = getValueOrEmpty(resultSet, CONTENT);
                    String answerId = getValueOrEmpty(resultSet, MSG_ID);
                    String messageType = getValueOrEmpty(resultSet, TYPE);
                    String sid = getZsid(resultSet);
                    String zoid = getZoid(resultSet);
                    String zlvl = getValueOrEmpty(resultSet, ZLVL);
                    MSGMessage msgChannel = new MSGMessage(content, answerId, MSGMessageType.valueOf(messageType));
                    objects.add(msgChannel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objects;
    }

    @SneakyThrows
    private List<MSGChannel> processResultSetToMSGChannels(ResultSet resultSet) {
        List<MSGChannel> objects = new ArrayList<>();
        try {
            while (resultSet.next()) {
                try {
                    String subject = getValueOrEmpty(resultSet, SUBJECT);
                    MSGChannelStatus status = MSGChannelStatus.valueOf(getValueOrEmpty(resultSet, STATUS));
                    String objId = getValueOrEmpty(resultSet, OBJ_ID);
                    String descr = getValueOrEmpty(resultSet, DESCRIPTION);
                    String sid = getZsid(resultSet);
                    String zoid = getZoid(resultSet);
                    String zlvl = getValueOrEmpty(resultSet, ZLVL);
                    MSGChannel msgChannel = new MSGChannel(subject, objId, status);
                    objects.add(msgChannel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objects;
    }
}
