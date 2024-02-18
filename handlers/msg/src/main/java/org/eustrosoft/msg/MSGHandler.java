/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.BasicService;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.core.db.ExecStatus;
import org.eustrosoft.core.db.util.DBUtils;
import org.eustrosoft.date.DateTimeZone;
import org.eustrosoft.handlers.sam.dto.UserDTO;
import org.eustrosoft.msg.dao.MSGDao;
import org.eustrosoft.msg.model.MSGChannel;
import org.eustrosoft.msg.model.MSGMessage;
import org.eustrosoft.msg.ranges.MSGChannelStatus;
import org.eustrosoft.msg.ranges.MSGMessageType;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.sam.model.User;
import org.eustrosoft.spec.Constants;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.eustrosoft.spec.Constants.ERR_OK;
import static org.eustrosoft.spec.Constants.ERR_UNEXPECTED;
import static org.eustrosoft.spec.Constants.MSG_UNEXPECTED;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_MSG;

@Handler(SUBSYSTEM_MSG)
public final class MSGHandler extends BasicService implements BasicHandler {
    private QDBPConnection poolConnection;

    public MSGHandler() {
    }

    @Override
    public BasicResponseBlock processRequest(BasicRequestBlock requestBlock) throws Exception {
        QDBPSession session = new SessionProvider(getRequest(), getResponse()).getSession();
        this.poolConnection = session.getConnection();
        MSGRequestBlock msgRequestBlock = (MSGRequestBlock) requestBlock;
        MsgParams params = msgRequestBlock.getParams();
        MSGResponseBlock msgResponseBlock = new MSGResponseBlock();
        msgResponseBlock.setE(ERR_OK);
        msgResponseBlock.setM(Constants.MSG_OK);
        String requestType = msgRequestBlock.getR();
        msgResponseBlock.setR(requestType);
        switch (requestType) {
            case Constants.REQUEST_CHATS:
                List<String> statuses = params == null ? Collections.emptyList() : params.getStatuses();
                msgResponseBlock.setChats(getChats(statuses));
                break;
            case Constants.REQUEST_UPDATE:
                List<String> ststa = params == null ? Collections.emptyList() : params.getStatuses();
                msgResponseBlock.setChats(getChatsVersions(ststa));
                break;
            case Constants.REQUEST_CHAT:
                List<MSGMessage> chatMessages = getChatMessages(params.getZoid());
                msgResponseBlock.setMessages(chatMessages);
                break;
            case Constants.REQUEST_CREATE:
                createChat(params.getZoid(), params.getSlvl(), params.getZsid(), params.getSubject(), params.getContent());
                break;
            case Constants.REQUEST_SEND:
                String message = createMessage(params);
                if (message == null) {
                    msgResponseBlock.setE(ERR_UNEXPECTED);
                    msgResponseBlock.setM(MSG_UNEXPECTED);
                }
                break;
            case Constants.REQUEST_EDIT:
                updateMessage(
                        params.getZoid(), params.getZrid(), params.getContent(),
                        params.getReference(), MSGMessageType.of(params.getType())
                );
                break;
            case Constants.REQUEST_DELETE_MSG:
            case Constants.REQUEST_DELETE:
                deleteMessage(params.getZoid(), params.getZrid());
                break;
            case Constants.REQUEST_DELETE_CH:
                deleteChannel(params.getZoid(), params.getZver());
                break;
            case Constants.REQUEST_CHANGE:
                changeChannelStatus(
                        params.getZoid(), params.getZrid(), params.getSubject(),
                        params.getReference(), MSGChannelStatus.of(params.getStatus())
                );
                break;
            default:
                msgResponseBlock.setE(ERR_UNEXPECTED);
                msgResponseBlock.setM(MSG_UNEXPECTED);
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

    public List<MSGChannel> getChatsVersions(List<String> statuses) throws SQLException {
        MSGDao functions = new MSGDao(poolConnection);
        List<MSGChannel> channels = new ArrayList<>();
        ResultSet chatsResultSet = functions.getChatsVersions(MSGChannelStatus.of(statuses));
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

    public String createChat(Long objId, Short slvl, Long sid, String subject, String content) throws Exception {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject can not be null or empty");
        }
        MSGDao functions = new MSGDao(poolConnection);
        MSGChannel newChannel = new MSGChannel(objId, null, null, subject, objId, MSGChannelStatus.N);
        newChannel.setZlvl(slvl);
        newChannel.setZsid(sid);
        ExecStatus chat = functions.createChat(newChannel);
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
        SamDAO samDAO = new SamDAO(poolConnection);
        while (resultSet.next()) {
            MSGMessage msgMessage = new MSGMessage();
            msgMessage.fillFromResultSet(resultSet);
            Timestamp created = resultSet.getTimestamp(DBConstants.ZDATE);
            if (created != null) {
                msgMessage.setCreated(new DateTimeZone(created).toString());
            }
            Long userId = DBUtils.getLongValueOrEmpty(resultSet, DBConstants.ZUID);
            User userById = samDAO.getUserById(userId);
            msgMessage.setUser(
                    new UserDTO(
                            userById.getId(),
                            userById.getUsername(),
                            userById.getFullName(),
                            null,
                            null
                    )
            );
            objects.add(msgMessage);
        }
        if (resultSet != null) {
            resultSet.close();
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
                // ex.printStackTrace();
            }
        }
        resultSet.close();
        Collections.reverse(objects);
        return objects;
    }
}
