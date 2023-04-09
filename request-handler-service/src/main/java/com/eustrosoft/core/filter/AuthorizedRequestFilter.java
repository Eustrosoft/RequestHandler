package com.eustrosoft.core.filter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import static com.eustrosoft.core.handlers.responses.ResponseLang.en_US;

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
        chain.doFilter(req, resp);
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
                resp.setContentType("application/json");
                PrintWriter writer = response.getWriter();
                writer.println(new Gson().toJson(
                        getUnauthorizedResponse()
                ));
                writer.flush();
                writer.close();
            } else {
                chain.doFilter(req, resp);
            }
        }
    }

    public void destroy() {
    }

    private JsonObject getUnauthorizedResponse() {
        JsonObject object = new JsonObject();
        object.addProperty("l", en_US);
        JsonObject response = new JsonObject();
        response.addProperty("m", "Unauthorized");
        response.addProperty("e", 401);
        response.addProperty("s", "login");
        response.addProperty("r", "login");
        object.add("r", response);
        return object;
    }
}
