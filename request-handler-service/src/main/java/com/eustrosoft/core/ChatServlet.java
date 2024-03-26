/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core;

import com.eustrosoft.core.config.ChatConfiguration;
import com.eustrosoft.core.handlers.msg.MSGHandler;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(configurator = ChatConfiguration.class, value = "/api/chat")
public class ChatServlet extends HttpServlet {
    private static Set<Session> userSessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        Map<String, Object> userProperties = config.getUserProperties();
        userSessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // send message to specific chat

        try {


        } catch (Exception ex) {

        }
        for (Session ses : userSessions) {
            ses.getAsyncRemote().sendText(message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        userSessions.remove(session);
    }

}
