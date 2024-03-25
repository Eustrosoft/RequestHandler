package com.eustrosoft.core.config;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.HandshakeRequest;

@WebListener
public class WsRequestListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        request.getSession();

        HttpServletResponse response = (HttpServletResponse) request.getAttribute("response");

        request.setAttribute("request", request);
        request.setAttribute("response", response);

        HandshakeRequest handshakeRequest = (HandshakeRequest) request.getAttribute("javax.websocket.server.HandshakeRequest");
        if (handshakeRequest != null) {

        }
    }
}
