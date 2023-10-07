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
import com.eustrosoft.core.tools.DateTimeZone;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import static com.eustrosoft.core.constants.Constants.REQUEST_DELETE_CH;
import static com.eustrosoft.core.constants.Constants.REQUEST_DELETE_MSG;
import static com.eustrosoft.core.constants.Constants.REQUEST_EDIT;
import static com.eustrosoft.core.constants.Constants.REQUEST_SEND;
import static com.eustrosoft.core.constants.Constants.UNKNOWN;
import static com.eustrosoft.core.constants.DBConstants.CONTENT;
import static com.eustrosoft.core.constants.DBConstants.MSG_ID;
import static com.eustrosoft.core.constants.DBConstants.TYPE;
import static com.eustrosoft.core.constants.DBConstants.ZDATE;
import static com.eustrosoft.core.constants.DBConstants.ZOID;
import static com.eustrosoft.core.constants.DBConstants.ZRID;
import static com.eustrosoft.core.constants.DBConstants.ZUID;
import static com.eustrosoft.core.constants.DBConstants.ZVER;

public final class MSGHandler implements Handler {
    private QDBPConnection poolConnection;

    public MSGHandler() {
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        QDBPSession session = new SessionProvider(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                .getSession();
        this.poolConnection = session.getConnection();
        MSGRequestBlock msgRequestBlock = (MSGRequestBlock) requestBlock;
        MsgParams params = msgRequestBlock.getParams();
        MSGResponseBlock msgResponseBlock = new MSGResponseBlock();
        msgResponseBlock.setE(ERR_OK);
        msgResponseBlock.setErrMsg(MSG_OK);
        String requestType = msgRequestBlock.getR();
        msgResponseBlock.setResponseType(requestType);
        // TODO
        switch (requestType) {
            case REQUEST_CHATS:
                List<String> statuses = params == null ? Collections.emptyList() : params.getStatuses();
                msgResponseBlock.setChats(getChats(statuses));
                break;
            case REQUEST_CHAT:
                List<MSGMessage> chatMessages = getChatMessages(params.getZoid());
                msgResponseBlock.setMessages(chatMessages);
                break;
            case REQUEST_CREATE:
                createChat(params.getZoid(), params.getSlvl(), params.getSubject(), params.getContent());
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
            case REQUEST_DELETE_MSG:
            case REQUEST_DELETE:
                deleteMessage(params.getZoid(), params.getZrid());
                break;
            case REQUEST_DELETE_CH:
                deleteChannel(params.getZoid(), params.getZver());
                break;
            case REQUEST_CHANGE:
                changeChannelStatus(
                        params.getZoid(), params.getZrid(), params.getSubject(),
                        params.getReference(), MSGChannelStatus.of(params.getStatus())
                );
                break;
            default:
                msgResponseBlock.setE(1);
                msgResponseBlock.setErrMsg("Has no this request type");
                break;
        }
        return msgResponseBlock;
    }

    public List<MSGChannel> getChats(List<String> statuses) throws SQLException {
        MSGDao functions = new MSGDao(poolConnection);
        List<MSGChannel> channels = new ArrayList<>();
        ResultSet chatsResultSet = functions.getChats(MSGChannelStatus.of(statuses));
        if (chatsResultSet != null) {
            channels = processResultSetToMSGChannels(chatsResultSet);
            chatsResultSet.close();
        }
        return channels;
    }

    public List<MSGMessage> getChatMessages(Long chatId) throws Exception {
        MSGDao functions = new MSGDao(poolConnection);
        List<MSGMessage> messages = new ArrayList<>();
        ResultSet chatsResultSet = functions.getMessages(chatId);
        if (chatsResultSet != null) {
            messages = processResultSetToMSGMessage(chatsResultSet);
            chatsResultSet.close();
        }
        return messages;
    }

    public String createChat(Long objId, Integer slvl, String subject, String content) throws Exception {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject can not be null or empty");
        }
        MSGDao functions = new MSGDao(poolConnection);
        ExecStatus chat = functions.createChat(new MSGChannel(subject, objId, MSGChannelStatus.N), slvl);
        if (content != null && !content.trim().isEmpty()) {
            functions.createMessage(
                    chat.getZoid(),
                    new MSGMessage(
                            content,
                            null,
                            MSGMessageType.M
                    )
            );
        }
        return chat.getZoid().toString();
    }

    public String createMessage(MsgParams params) throws Exception {
        if (params.getContent() == null || params.getContent().isEmpty()) {
            throw new IllegalArgumentException("Message content can not be null or empty");
        }
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

    public void updateMessage(Long zoid, Long zrid, String content, Long answerId, MSGMessageType type) throws Exception {
        MSGDao functions = new MSGDao(poolConnection);
        functions.updateMessage(new MSGMessage(zoid, null, zrid, content, answerId, type));
    }

    private void deleteChannel(Long zoid, Long zver) throws Exception {
        MSGDao functions = new MSGDao(poolConnection);
        functions.deleteChannel(zoid, zver);
    }

    public void deleteMessage(Long chatId, Long messageId) throws Exception {
        MSGDao functions = new MSGDao(poolConnection);
        functions.deleteMessage(chatId, messageId);
    }

    public void changeChannelStatus(Long zoid, Long zrid, String content, Long docId, MSGChannelStatus status) throws Exception {
        MSGDao functions = new MSGDao(poolConnection);
        functions.updateChannel(new MSGChannel(zoid, null, zrid, content, docId, status));
    }

    private List<MSGMessage> processResultSetToMSGMessage(ResultSet resultSet) throws Exception {
        List<MSGMessage> objects = new ArrayList<>();
        Map<Long, User> userMapping = new HashMap<>();
        SamDAO samDAO = new SamDAO(poolConnection);
        while (resultSet.next()) {
            Long zoid = resultSet.getLong(ZOID);
            Long zver = resultSet.getLong(ZVER);
            Long zrid = resultSet.getLong(ZRID);
            String content = resultSet.getString(CONTENT);
            Long answerId = resultSet.getLong(MSG_ID);
            String messageType = resultSet.getString(TYPE);
            Long userId = resultSet.getLong(ZUID);
            Timestamp created = resultSet.getTimestamp(ZDATE); // todo
            User user = null;
            if (!userMapping.containsKey(userId)) {
                user = new User();
                try {
                    user.fillFromResultSet(samDAO.getUserResultSetById(userId));
                    userMapping.put(userId, user);
                } catch (Exception ex) {
                    user.setId(userId);
                    user.setUsername(String.format("%s_%d", UNKNOWN, userId));
                    user.setFullName(String.format("%s_%d", UNKNOWN, userId));
                    userMapping.put(userId, user);
                }
            } else {
                user = userMapping.get(userId);
            }
            MSGMessage msgChannel = new MSGMessage(
                    zoid, zver, zrid, new DateTimeZone(created),
                    content, answerId, MSGMessageType.of(messageType)
            );
            msgChannel.setUser(UserDTO.fromUser(user));
            objects.add(msgChannel);
        }
        return objects;
    }

    private List<MSGChannel> processResultSetToMSGChannels(ResultSet resultSet) throws SQLException {
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
