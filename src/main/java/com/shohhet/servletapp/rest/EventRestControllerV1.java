package com.shohhet.servletapp.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shohhet.servletapp.service.EventService;
import com.shohhet.servletapp.dto.GetEventRequestDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static com.shohhet.servletapp.utils.ServletUtils.isInteger;

@WebServlet(urlPatterns = {"/api/events/*", "/api/events"})
public class EventRestControllerV1 extends HttpServlet {
    private EventService eventService;

    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        eventService = (EventService) getServletContext().getAttribute(EventService.class.getSimpleName());
        gson = new GsonBuilder().setPrettyPrinting().create();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        path = path == null || path.isEmpty() ? "" : path.substring(1);
        var writer = resp.getWriter();
        if (path.isEmpty()) {
            var eventDtos = eventService.getAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json; charset=UTF-8");
            writer.write(gson.toJson(eventDtos, new TypeToken<List<GetEventRequestDto>>() {
            }.getType()));
        } else if (Pattern.matches("^\\d+$", path) && isInteger(path)) {
            int id = Integer.parseInt(path);
            eventService.getById(id).ifPresentOrElse(
                    getEventRequestDto -> {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.setContentType("application/json; charset=UTF-8");
                        writer.write(gson.toJson(getEventRequestDto));
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writer.write("Event not found.");
                    }
            );
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write("Resource not found");
        }
    }
}
