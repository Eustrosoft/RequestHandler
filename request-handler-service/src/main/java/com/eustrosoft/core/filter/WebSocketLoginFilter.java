/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.filter;

import com.eustrosoft.core.tools.LoginChecker;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/api/chat/*")
public class WebSocketLoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain
    ) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            LoginChecker.checkLogin(request, response, "MSG.C");
            AddParamsToRequest addParamsToRequest = new AddParamsToRequest(request);
            addParamsToRequest.setRequest(request);
            addParamsToRequest.setResponse(response);
            filterChain.doFilter(
                    addParamsToRequest,
                    response
            );
        } catch (Exception exception) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is forbidden");
        }
    }

    class AddParamsToRequest extends HttpServletRequestWrapper {

        private HttpServletRequest request;
        private HttpServletResponse response;

        public AddParamsToRequest(HttpServletRequest request) {
            super(request);
        }

        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public void setResponse(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public Object getAttribute(String name) {
            if (name.equalsIgnoreCase("request")) {
                return this.request;
            }
            if (name.equalsIgnoreCase("response")) {
                return this.response;
            }

            return super.getAttribute(name);
        }
    }
}
