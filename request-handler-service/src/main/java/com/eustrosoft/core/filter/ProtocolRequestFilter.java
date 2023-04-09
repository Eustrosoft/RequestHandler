package com.eustrosoft.core.filter;

import com.eustrosoft.core.tools.QJson;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Stream;

import static com.eustrosoft.core.handlers.responses.ResponseLang.en_US;

@WebFilter(
        urlPatterns = {"/api/*"},
        filterName = "ProtocolRequestFilter",
        description = "Protocol Authorized Filter"
)
public class ProtocolRequestFilter implements Filter {
    private ServletContext servletContext;

    public void init(FilterConfig filterConfig) {
        servletContext = filterConfig.getServletContext();
    }

    public void doFilter(ServletRequest req,
                         ServletResponse resp,
                         FilterChain chain
    ) throws IOException, ServletException {
        chain.doFilter(req, resp);
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) resp;
//        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request); // TODO
//
//        if (hasSession(request) || isLoginSubsystem(wrapper)) {
//            chain.doFilter(wrapper.getRequest(), resp);
//        } else {
//            this.servletContext.log("Unauthorized access request");
//            resp.setContentType("application/json");
//            PrintWriter writer = response.getWriter();
//            writer.println(new Gson().toJson(
//                    getUnauthorizedResponse()
//            ));
//            writer.flush();
//            writer.close();
//        }
    }

    public void destroy() {
    }

    private boolean hasSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null;
    }

    private boolean isLoginSubsystem(ServletRequestWrapper wrapper) throws IOException {
        Gson gson = new Gson();
        Map values = gson.fromJson(wrapper.getReader(), Map.class);
        List<Map<String, Object>> requests = (List<Map<String, Object>>) values.get("r");
        for (Map<String, Object> entry : requests) {
            String subsystem = (String) entry.get("s");
            return subsystem.equalsIgnoreCase("login");
        }
        return false;
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
