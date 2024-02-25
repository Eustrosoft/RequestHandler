/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg;

import org.eustrosoft.constants.Constants;
import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.annotations.Handler;
import org.eustrosoft.core.date.DateTimeZone;
import org.eustrosoft.core.db.ExecStatus;
import org.eustrosoft.core.db.util.DBUtils;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.BasicRequestBlock;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.core.response.ResponseLang;
import org.eustrosoft.core.response.basic.ListRawResponseData;
import org.eustrosoft.handlers.msg.dto.MSGRequestBlock;
import org.eustrosoft.handlers.msg.dto.MSGResponseBlock;
import org.eustrosoft.handlers.msg.dto.MsgParams;
import org.eustrosoft.handlers.msg.dto.base.MSGChannelStatus;
import org.eustrosoft.handlers.msg.dto.base.MSGMessageType;
import org.eustrosoft.msg.dao.MSGDao;
import org.eustrosoft.msg.model.MSGChannel;
import org.eustrosoft.msg.model.MSGMessage;
import org.eustrosoft.msg.transform.MSGChannelToDto;
import org.eustrosoft.msg.transform.MSGMessageToDto;
import org.eustrosoft.providers.RequestContextHolder;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.sam.transform.UserDtoFromUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.eustrosoft.constants.Constants.ERR_UNEXPECTED;
import static org.eustrosoft.constants.Constants.SUBSYSTEM_MSG;

@Handler(SUBSYSTEM_MSG)
public final class MSGHandler implements BasicHandler {
    private QDBPConnection poolConnection;

    public MSGHandler() {
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        RequestContextHolder.ServletAttributes attributes = RequestContextHolder.getAttributes();
        QDBPSession session = new SessionProvider(attributes.getRequest(), attributes.getResponse())
                .getSession();
        this.poolConnection = session.getConnection();
        BasicRequestBlock<MSGRequestBlock> rb = (BasicRequestBlock) requestBlock;
        String requestType = rb.getR();

        rb.setData(new MSGRequestBlock());
        MSGRequestBlock data = rb.getData();

        MSGResponseBlock msgResponseBlock = new MSGResponseBlock(requestType);

        msgResponseBlock.setM(Constants.MSG_OK);
        msgResponseBlock.setE(Constants.ERR_OK);
        msgResponseBlock.setL(ResponseLang.EN_US.getLang());
        switch (requestType) {
            case Constants.REQUEST_CHATS:
                List<String> statuses = data == null ? Collections.emptyList() : data.getStatuses();
                msgResponseBlock.setData(
                        new ListRawResponseData(
                                "channels",
                                getChats(statuses).stream()
                                        .map(new MSGChannelToDto())
                                        .map(ch -> {
                                            try {
                                                return ch.convertToString();
                                            } catch (Exception ex) {
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList()))
                );
                break;
            case Constants.REQUEST_UPDATE:
                List<String> ststa = data == null ? Collections.emptyList() : data.getStatuses();
                msgResponseBlock.setData(
                        new ListRawResponseData(
                                "channels",
                                getChatsVersions(ststa).stream()
                                        .map(new MSGChannelToDto())
                                        .map(ch -> {
                                            try {
                                                return ch.convertToString();
                                            } catch (Exception ex) {
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList()))
                );
                break;
            case Constants.REQUEST_CHAT:
                msgResponseBlock.setData(
                        new ListRawResponseData(
                                "messages",
                                getChatMessages(data.getZOID()).stream()
                                        .map(new MSGMessageToDto())
                                        .map(ch -> {
                                            try {
                                                return ch.convertToString();
                                            } catch (Exception ex) {
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList()))
                );
                break;
            case Constants.REQUEST_CREATE:
                createChat(data.getZOID(), data.getZLVL(), data.getZSID(), data.getSubject(), data.getContent());
                break;
            case Constants.REQUEST_SEND:
                String message = createMessage(data);
                if (message == null) {
                    msgResponseBlock.setE(ERR_UNEXPECTED);
                    msgResponseBlock.setM("Error while creating message");
                }
                break;
            case Constants.REQUEST_EDIT:
                updateMessage(
                        data.getZOID(), data.getZRID(), data.getContent(),
                        data.getReference(), MSGMessageType.A.of(data.getType())
                );
                break;
            case Constants.REQUEST_DELETE_MSG:
            case Constants.REQUEST_DELETE:
                deleteMessage(data.getZOID(), data.getZRID());
                break;
            case Constants.REQUEST_DELETE_CH:
                deleteChannel(data.getZOID(), data.getZVER());
                break;
            case Constants.REQUEST_CHANGE:
                changeChannelStatus(
                        data.getZOID(), data.getZRID(), data.getSubject(),
                        data.getReference(), MSGChannelStatus.of(data.getStatus())
                );
                break;
            default:
                msgResponseBlock.setE(ERR_UNEXPECTED);
                msgResponseBlock.setM("Has no this request type");
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
        MSGChannel newChannel = new MSGChannel(-1L, -1L, -1L, subject, objId, MSGChannelStatus.N);
        newChannel.setZLVL(slvl);
        newChannel.setZSID(sid);
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
                params.getZOID(),
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
            msgMessage.setUser(
                    new UserDtoFromUser().apply(
                            samDAO.getUserById(userId)
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
