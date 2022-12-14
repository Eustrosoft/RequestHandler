package com.eustrosoft.core.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
        urlPatterns = {"/*"},
        filterName = "RequestLoggingFilter",
        description = "Logging Filter"
)
public class CorsAllowFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        ((HttpServletResponse) response).addHeader("Access-Control-Allow-Headers", "*");
        ((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", "*");
        filterChain.doFilter(request, response);
    }
}
