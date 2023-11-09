package com.shohhet.servletapp.servlet;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shohhet.servletapp.service.UserService;
import com.shohhet.servletapp.service.dto.userDto.UserNameDto;
import com.shohhet.servletapp.service.dto.userDto.UserDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import static com.shohhet.servletapp.utils.ServletUtils.isInteger;

@WebServlet(urlPatterns = "/api/users/*")
public class UserServlet extends HttpServlet {
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
        String path = req.getPathInfo().substring(1);
        var writer = resp.getWriter();
        if (path.isEmpty()) {
            var userDtos = userService.getAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json; charset=UTF-8");
            writer.write(gson.toJson(userDtos, new TypeToken<List<UserDto>>() {
            }.getType()));
        } else if (Pattern.matches("^\\d+$", path) && isInteger(path)) {
            int id = Integer.parseInt(path);
            userService.getById(id).ifPresentOrElse(
                    fileDto -> {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.setContentType("application/json; charset=UTF-8");
                        writer.write(gson.toJson(fileDto));
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writer.write("User not found.");
                    }
            );
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write("Resource not found");
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo().substring(1);
        var writer = resp.getWriter();
        if (path.isEmpty()) {
            var userNameDto = gson.fromJson(req.getReader(), UserNameDto.class);
            userService.add(userNameDto).ifPresentOrElse(
                    userDto -> {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.setContentType("application/json; charset=UTF-8");
                        writer.write(gson.toJson(userDto));
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
            var userNameDto = gson.fromJson(req.getReader(), UserNameDto.class);
            userService.update(new UserDto(userId, userNameDto.name()))
                    .ifPresentOrElse(
                            userDto -> {
                                resp.setStatus(HttpServletResponse.SC_OK);
                                resp.setContentType("application/json; charset=UTF-8");
                                writer.write(gson.toJson(userDto));
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