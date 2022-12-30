package com.eustrosoft.core.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebFilter(
        urlPatterns = {"/api/*"},
        filterName = "AuthorizedRequestFilter",
        description = "Authorized Filter"
)
public class AuthorizedRequestFilter implements Filter {
    private Set<String> excluded = new HashSet<>();
    private ServletContext servletContext;

    public void init(FilterConfig filterConfig) {
        excluded.add("/api/login");
        servletContext = filterConfig.getServletContext();
    }

    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getRequestURI()
                .substring(request.getContextPath().length())
                .replaceAll("[/]+$", "");
        if (excluded.contains(path)) {
            chain.doFilter(req, resp);
        } else {
            HttpSession session = request.getSession(false);
            if (session == null) {
                this.servletContext.log("Unauthorized access request");
                response.sendError(501);
            } else {
                chain.doFilter(req, resp);
            }
        }
    }

    public void destroy() {
    }

}
