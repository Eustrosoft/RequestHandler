package org.eustrosoft.providers;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestContextHolder {

    private static final ThreadLocal<ServletAttributes> requestHolder = new ThreadLocal<>();

    public static void setRequest(ServletAttributes request) {
        requestHolder.set(request);
    }

    public static ServletAttributes getAttributes() {
        return requestHolder.get();
    }

    public static void clear() {
        requestHolder.remove();
    }

    public static class ServletAttributes {
        private final HttpServletResponse response;
        private final HttpServletRequest request;

        public ServletAttributes(HttpServletResponse response, HttpServletRequest request) {
            this.response = response;
            this.request = request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }

        public HttpServletRequest getRequest() {
            return request;
        }
    }
}
