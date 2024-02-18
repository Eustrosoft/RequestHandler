package org.eustrosoft.dispatcher;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.dispatcher.context.HandlersConfig;
import org.eustrosoft.dispatcher.context.HandlersContext;
import org.eustrosoft.json.QJson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class ConfigurationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        QJson json = new QJson();
        json.parseJSONReader(req.getReader());
        String path = json.getItemString("path");
        if (path != null || !path.isEmpty()) {
            HandlersContext context = HandlersContext.getInstance(new HandlersConfig(path));
            context.initLazy();
            resp.getWriter().println("Properties was reloaded successfully");
            printHandlers(resp.getWriter(), context);
            resp.getWriter().flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HandlersContext context = HandlersContext.getInstance();
        PrintWriter writer = resp.getWriter();
        printHandlers(writer, context);
        writer.flush();
    }

    private void printHandlers(PrintWriter writer, HandlersContext context) {
        Map<String, Class<?>> handlersMap = context.getHandlersMap();
        writer.println("Handlers loaded: ");
        for (Map.Entry<String, Class<?>> entry : handlersMap.entrySet()) {
            writer.println(entry.getKey() + " handler. Location: " + entry.getValue());
        }
    }
}
