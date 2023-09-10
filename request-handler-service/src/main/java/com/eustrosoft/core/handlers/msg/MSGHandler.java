/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.msg;

import com.eustrosoft.core.db.ExecStatus;
import com.eustrosoft.core.db.dao.MSGDao;
import com.eustrosoft.core.db.dao.SamDAO;
import com.eustrosoft.core.dto.UserDTO;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.model.MSGChannel;
import com.eustrosoft.core.model.MSGMessage;
import com.eustrosoft.core.model.ranges.MSGChannelStatus;
import com.eustrosoft.core.model.ranges.MSGMessageType;
import com.eustrosoft.core.model.user.User;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.core.providers.context.UsersContext;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.eustrosoft.core.constants.Constants.ERR_OK;
import static com.eustrosoft.core.constants.Constants.MSG_OK;
import static com.eustrosoft.core.constants.Constants.REQUEST_CHANGE;
import static com.eustrosoft.core.constants.Constants.REQUEST_CHAT;
import static com.eustrosoft.core.constants.Constants.REQUEST_CHATS;
import static com.eustrosoft.core.constants.Constants.REQUEST_CREATE;
import static com.eustrosoft.core.constants.Constants.REQUEST_DELETE;
import static com.eustrosoft.core.constants.Constants.REQUEST_EDIT;
import static com.eustrosoft.core.constants.Constants.REQUEST_SEND;

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
                List<MSGMessage> chatMessages = getChatMessages(params.getZoid());
                msgResponseBlock.setMessages(chatMessages);
                break;
            case REQUEST_CREATE:
                createChat(params.getZoid(), params.getSlvl(), params.getContent());
                break;
            case REQUEST_SEND:
                String message = createMessage(params);
                if (message == null) {
                    msgResponseBlock.setErrCode((short) 1);
                    msgResponseBlock.setErrMsg("Error while creating message");
                }
                break;
            case REQUEST_EDIT:
                updateMessage(
                        params.getZoid(), params.getZrid(), params.getContent(),
                        params.getReference(), MSGMessageType.of(params.getType())
                );
                break;
            case REQUEST_DELETE:
                deleteMessage(params.getZoid(), params.getZrid());
                break;
            case REQUEST_CHANGE:
                changeChannelStatus(
                        params.getZoid(), params.getZrid(), params.getContent(),
                        params.getReference(), MSGChannelStatus.of(params.getType())
                );
                break;
            default:
                msgResponseBlock.setE(1);
                msgResponseBlock.setErrMsg("Has no this request type");
                break;
        }
        return msgResponseBlock;
    }

    public List<MSGChannel> getChats() throws SQLException {
        MSGDao functions = new MSGDao(poolConnection);
        List<MSGChannel> channels = new ArrayList<>();
        ResultSet chatsResultSet = functions.getChats();
        if (chatsResultSet != null) {
            channels = processResultSetToMSGChannels(chatsResultSet);
            chatsResultSet.close();
        }
        return channels;
    }

    public List<MSGMessage> getChatMessages(Long chatId) throws SQLException {
        MSGDao functions = new MSGDao(poolConnection);
        List<MSGMessage> messages = new ArrayList<>();
        ResultSet chatsResultSet = functions.getMessages(chatId);
        if (chatsResultSet != null) {
            messages = processResultSetToMSGMessage(chatsResultSet);
            chatsResultSet.close();
        }
        Collections.reverse(messages);
        return messages;
    }

    public String createChat(Long objId, Integer slvl, String subject) {
        MSGDao functions = new MSGDao(poolConnection);
        ExecStatus chat = functions.createChat(new MSGChannel(subject, objId, MSGChannelStatus.N), slvl);
        return chat.getZoid().toString();
    }

    public String createMessage(MsgParams params) {
        MSGDao functions = new MSGDao(poolConnection);
        ExecStatus message = functions.createMessage(
                params.getZoid(),
                new MSGMessage(
                        params.getContent(),
                        params.getReference(),
                        MSGMessageType.of(params.getType())
                )
        );
        if (message.isOk()) {
            return message.getZoid().toString();
        }
        return null;
    }

    public void updateMessage(Long zoid, Long zrid, String content, Long answerId, MSGMessageType type) {
        MSGDao functions = new MSGDao(poolConnection);
        functions.updateMessage(new MSGMessage(zoid, null, zrid, content, answerId, type));
    }

    public void deleteMessage(Long chatId, Long messageId) {
        MSGDao functions = new MSGDao(poolConnection);
        functions.deleteMessage(chatId, messageId);
    }

    public void changeChannelStatus(Long zoid, Long zrid, String content, Long docId, MSGChannelStatus status) {
        MSGDao functions = new MSGDao(poolConnection);
        functions.updateChannel(new MSGChannel(zoid, null, zrid, content, docId, status));
    }

    @SneakyThrows
    private List<MSGMessage> processResultSetToMSGMessage(ResultSet resultSet) {
        List<MSGMessage> objects = new ArrayList<>();
        Map<Long, User> userMapping = new HashMap<>();
        SamDAO samDAO = new SamDAO(poolConnection);
        try {
            while (resultSet.next()) {
                try {
                    String str = resultSet.getString(1);
                    String[] splitted = str.substring(1, str.length() - 1).split(",");
                    Long zoid = Long.valueOf(splitted[0]);
                    Long zver = Long.valueOf(splitted[1]);
                    Long zrid = Long.valueOf(splitted[2]);
                    String content = splitted[4];
                    Long answerId = getLongOrNull(splitted[5]);
                    String messageType = splitted[6];
                    Long userId = getLongOrNull(splitted[7]);
                    User user = null;
                    if (!userMapping.containsKey(userId)) {
                        user = User.fromResultSet(samDAO.getUserResultSetById(userId));
                    } else {
                        user = userMapping.get(userId);
                    }
                    MSGMessage msgChannel = new MSGMessage(
                            zoid, zver, zrid, content, answerId, MSGMessageType.of(messageType)
                    );
                    msgChannel.setUser(UserDTO.fromUser(user));
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
        while (resultSet.next()) {
            try {
                MSGChannel msgChannel = new MSGChannel();
                msgChannel.fillFromResultSet(resultSet);
                objects.add(msgChannel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        resultSet.close();
        Collections.reverse(objects);
        return objects;
    }

    private Long getLongOrNull(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ex) {
            return null;
        }
    }
}
