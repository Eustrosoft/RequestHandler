package org.eustrosoft.core;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
        urlPatterns = {"/*"},
        filterName = "RequestContextHandler",
        description = "Context Handler"
)
public class RequestContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        try {
            if (servletRequest instanceof HttpServletRequest
                    && servletResponse instanceof HttpServletResponse) {
                RequestContextHolder.setRequest(
                        new RequestContextHolder.ServletAttributes(
                                (HttpServletResponse) servletResponse,
                                (HttpServletRequest) servletRequest
                        )
                );
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            RequestContextHolder.clear();
        }
    }
}
