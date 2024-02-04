package org.eustrosoft.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Service {
    default HttpServletRequest getRequest() {
        return RequestContextHolder.getAttributes().getRequest();
    }

    default HttpServletResponse getResponse() {
        return RequestContextHolder.getAttributes().getResponse();
    }
}
