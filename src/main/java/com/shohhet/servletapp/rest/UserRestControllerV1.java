package com.shohhet.servletapp.rest;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shohhet.servletapp.service.UserService;
import com.shohhet.servletapp.dto.CreateUserRequestDto;
import com.shohhet.servletapp.dto.GetAndUpdateUserRequestDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import static com.shohhet.servletapp.utils.ServletUtils.isInteger;

@WebServlet(urlPatterns = {"/api/v1/users/*", "/api/v1/users"})
public class UserRestControllerV1 extends HttpServlet {
    private UserService userService;
    private Gson gson;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = (UserService) config.getServletContext()
                .getAttribute(UserService.class.getSimpleName());
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        path = path == null || path.isEmpty() ? "" : path.substring(1);
        var writer = resp.getWriter();
        if (path.isEmpty()) {
            var userDtos = userService.getAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json; charset=UTF-8");
            writer.write(gson.toJson(userDtos, new TypeToken<List<GetAndUpdateUserRequestDto>>() {
            }.getType()));
        } else if (Pattern.matches("^\\d+$", path) && isInteger(path)) {
            int id = Integer.parseInt(path);
            userService.getById(id).ifPresentOrElse(
                    getAndUpdateUserRequestDto -> {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.setContentType("application/json; charset=UTF-8");
                        writer.write(gson.toJson(getAndUpdateUserRequestDto));
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.setContentType("text/plain; charset=UTF-8");
                        writer.write("User not found.");
                    }
            );
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("text/plain; charset=UTF-8");
            writer.write("Resource not found");
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        path = path == null || path.isEmpty() ? "" : path.substring(1);
        var writer = resp.getWriter();
        if (path.isEmpty()) {
            var userNameDto = gson.fromJson(req.getReader(), CreateUserRequestDto.class);
            userService.add(userNameDto).ifPresentOrElse(
                    getAndUpdateUserRequestDto -> {
                        resp.setStatus(HttpServletResponse.SC_CREATED);
                        resp.setContentType("application/json; charset=UTF-8");
                        writer.write(gson.toJson(getAndUpdateUserRequestDto));
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writer.write("User already exist");
                    }
            );
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo().substring(1);
        var writer = resp.getWriter();
        if (Pattern.matches("^\\d+$", path) && isInteger(path)) {
            var userId = Integer.parseInt(path);
            var userNameDto = gson.fromJson(req.getReader(), CreateUserRequestDto.class);
            userService.update(new GetAndUpdateUserRequestDto(userId, userNameDto.name()))
                    .ifPresentOrElse(
                            getAndUpdateUserRequestDto -> {
                                resp.setStatus(HttpServletResponse.SC_OK);
                                resp.setContentType("application/json; charset=UTF-8");
                                writer.write(gson.toJson(getAndUpdateUserRequestDto));
                            },
                            () -> {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writer.write("User not found.");
                            }
                    );

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo().substring(1);
        var writer = resp.getWriter();
        if (Pattern.matches("^\\d+$", path) && isInteger(path)) {
            var userId = Integer.parseInt(path);
            if (userService.delete(userId)) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/json; charset=UTF-8");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writer.write("User does not exist.");
            }
        }

    }

}