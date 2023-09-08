/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.msg;

import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.datasource.sources.model.MSGChannel;
import com.eustrosoft.datasource.sources.model.MSGMessage;
import com.eustrosoft.datasource.sources.ranges.MSGMessageType;
import com.eustrosoft.dbdatasource.core.DBFunctions;
import com.eustrosoft.dbdatasource.core.ExecStatus;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
        msgResponseBlock.setE(ERR_OK);
        msgResponseBlock.setErrMsg(MSG_OK);
        msgResponseBlock.setResponseType(requestType);
        // TODO
        switch (requestType) {
            case REQUEST_CHATS:
                List<MSGChannel> chats = getChats();
                msgResponseBlock.setChats(chats);
                break;
            case REQUEST_CHAT:
                List<MSGMessage> chatMessages = getChatMessages(params.getId());
                msgResponseBlock.setMessages(chatMessages);
                break;
            case REQUEST_CREATE:
                createChat(params.getId(), params.getSlvl(), params.getContent());
                break;
            case REQUEST_SEND:
                String message = createMessage(params);
                if (message == null) {
                    msgResponseBlock.setErrCode((short) 1);
                    msgResponseBlock.setErrMsg("Error while creating message");
                }
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
        DBFunctions functions = new DBFunctions(poolConnection);
        List<MSGChannel> channels = new ArrayList<>();
        ResultSet chatsResultSet = functions.getChats();
        if (chatsResultSet != null) {
            channels = processResultSetToMSGChannels(chatsResultSet);
            chatsResultSet.close();
        }
        return channels;
    }

    public List<MSGMessage> getChatMessages(String chatId) throws SQLException {
        DBFunctions functions = new DBFunctions(poolConnection);
        List<MSGMessage> messages = new ArrayList<>();
        ResultSet chatsResultSet = functions.getMessages(chatId);
        if (chatsResultSet != null) {
            messages = processResultSetToMSGMessage(chatsResultSet);
            chatsResultSet.close();
        }
        Collections.reverse(messages);
        return messages;
    }

    public String createChat(String objId, Integer slvl, String subject) {
        DBFunctions functions = new DBFunctions(poolConnection);
        ExecStatus chat = functions.createChat(subject, slvl, objId);
        return chat.getZoid().toString();
    }

    public String createMessage(MsgParams params) {
        DBFunctions functions = new DBFunctions(poolConnection);
        ExecStatus message = functions.createMessage(
                params.getId(), params.getContent(),
                MSGMessageType.of(params.getType()), params.getReference()
        );
        if (message.isOk()) {
            return message.getZoid().toString();
        }
        return null;
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
                    String zrid = getZrid(resultSet);
                    MSGMessage msgChannel = new MSGMessage(zrid, content, answerId, MSGMessageType.of(messageType));
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
                    String chStatus = getValueOrEmpty(resultSet, STATUS);
                    String objId = getValueOrEmpty(resultSet, OBJ_ID);
                    String descr = getValueOrEmpty(resultSet, DESCRIPTION);
                    String sid = getZsid(resultSet);
                    String zoid = getZoid(resultSet);
                    String zlvl = getValueOrEmpty(resultSet, ZLVL);
                    MSGChannel msgChannel = new MSGChannel(zoid, subject, objId, chStatus);
                    objects.add(msgChannel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        resultSet.close();
        Collections.reverse(objects);
        return objects;
    }
}
