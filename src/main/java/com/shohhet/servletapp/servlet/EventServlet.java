package com.shohhet.servletapp.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shohhet.servletapp.service.EventService;
import com.shohhet.servletapp.service.FileService;
import com.shohhet.servletapp.service.dto.eventDto.EventDto;
import com.shohhet.servletapp.service.dto.userDto.UserDto;
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

@WebServlet(urlPatterns = "/api/events/*")
public class EventServlet extends HttpServlet {
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
            writer.write(gson.toJson(eventDtos, new TypeToken<List<EventDto>>() {
            }.getType()));
        } else if (Pattern.matches("^\\d+$", path) && isInteger(path)) {
            int id = Integer.parseInt(path);
            eventService.getById(id).ifPresentOrElse(
                    eventDto -> {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.setContentType("application/json; charset=UTF-8");
                        writer.write(gson.toJson(eventDto));
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
