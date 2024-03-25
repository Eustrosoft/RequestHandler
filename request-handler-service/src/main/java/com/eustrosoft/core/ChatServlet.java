/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core;

import com.eustrosoft.core.config.ChatConfiguration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(configurator = ChatConfiguration.class, value = "/api/chat")
public class ChatServlet extends HttpServlet {
    private static Set<Session> userSessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // check user is loggined
//        try {
//            checkLogin(session.get, resp, SUBSYSTEM_CMS);
//        } catch (Exception ex) {
//            try {
//                printError(resp, getUnauthorizedResponse());
//            } catch (Exception exception) {
//                // ignored
//            }
//            return;
//        }
        try {
            HttpServletResponse response = (HttpServletResponse) config.getUserProperties().get(HttpServletResponse.class.getName());
        } catch (Exception ex) {

        }
        userSessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // send message to specific chat


        for (Session ses : userSessions) {
            ses.getAsyncRemote().sendText(message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        userSessions.remove(session);
    }

}
