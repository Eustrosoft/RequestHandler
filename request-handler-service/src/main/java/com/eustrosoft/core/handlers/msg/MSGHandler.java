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
import com.eustrosoft.datasource.sources.ranges.MSGChannelStatus;
import com.eustrosoft.dbdatasource.core.DBFunctions;
import com.eustrosoft.dbdatasource.core.DBStatements;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.Constants.REQUEST_CHANGE;
import static com.eustrosoft.core.Constants.REQUEST_CHAT;
import static com.eustrosoft.core.Constants.REQUEST_CHATS;
import static com.eustrosoft.core.Constants.REQUEST_CREATE;
import static com.eustrosoft.core.Constants.REQUEST_DELETE;
import static com.eustrosoft.core.Constants.REQUEST_EDIT;
import static com.eustrosoft.core.Constants.REQUEST_SEND;
import static com.eustrosoft.dbdatasource.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.dbdatasource.constants.DBConstants.OBJ_ID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.STATUS;
import static com.eustrosoft.dbdatasource.constants.DBConstants.SUBJECT;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZLVL;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.getValueOrEmpty;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.getZoid;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.getZsid;

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
                break;
            case REQUEST_CREATE:
                break;
            case REQUEST_SEND:
                break;
            case REQUEST_EDIT:
                break;
            case REQUEST_DELETE:
                break;
            case REQUEST_CHANGE:
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

    public void getChatMessages(String chatId) {
        String session = new QTISSessionCookie(req, resp).getCookieValue();
        DBFunctions functions = new DBFunctions(poolConnection);
        String userName = usersContext.getSQLUser(session).getUserName();
        User user = functions.getUserFromDB(userName);

        return;
    }

    public void createChat(String objId) {
        String session = new QTISSessionCookie(req, resp).getCookieValue();
        DBFunctions functions = new DBFunctions(poolConnection);
        String userName = usersContext.getSQLUser(session).getUserName();
        User user = functions.getUserFromDB(userName);
        return;
    }

    public void updateMessage(HttpServletRequest req, HttpServletResponse resp) {
        String session = new QTISSessionCookie(req, resp).getCookieValue();
        DBFunctions functions = new DBFunctions(poolConnection);
        String userName = usersContext.getSQLUser(session).getUserName();
        User user = functions.getUserFromDB(userName);
        return;
    }

    public void deleteMessage(HttpServletRequest req, HttpServletResponse resp) {
        String session = new QTISSessionCookie(req, resp).getCookieValue();
        DBFunctions functions = new DBFunctions(poolConnection);
        String userName = usersContext.getSQLUser(session).getUserName();
        User user = functions.getUserFromDB(userName);
        return;
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
