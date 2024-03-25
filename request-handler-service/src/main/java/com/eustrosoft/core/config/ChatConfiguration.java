package com.eustrosoft.core.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_MSG;
import static com.eustrosoft.core.tools.LoginChecker.checkLogin;

public class ChatConfiguration extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        try {
            HttpSession httpSession = (HttpSession) request.getHttpSession();

            // Store HttpServletResponse as a property in the HandshakeRequest
            config.getUserProperties().put(HttpServletResponse.class.getName(), httpSession);
        } catch (Exception exception) {

        }
    }

    private boolean isAuthorized(HandshakeRequest req) {
        try {
            HttpServletRequest request = (HttpServletRequest) req.getHttpSession();
            HttpServletResponse response = (HttpServletResponse) request.getAttribute("response");
            checkLogin(request, response, SUBSYSTEM_MSG);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
