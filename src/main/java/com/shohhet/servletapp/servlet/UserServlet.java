package com.shohhet.servletapp.servlet;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shohhet.servletapp.service.UserService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(urlPatterns = "/users/*")
public class UserServlet extends HttpServlet {
    private String message;
    private UserService userService;
    private Gson gson;

    public void init(ServletConfig config) {
        userService = (UserService) config.getServletContext()
                .getAttribute(UserService.class.getSimpleName());
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (Pattern.matches("/\\d+", path)) {
            Matcher matcher = Pattern.compile("\\d+").matcher(path);
            matcher.find();
            Integer id = Integer.valueOf(matcher.group());
            var out = response.getWriter();

            userService.get(id).ifPresentOrElse(
                    (user) -> {
                        response.setStatus(200);
                        response.setContentType("application/json; charset=UTF-8");
                        out.write(gson.toJson(user));
                    },
                    () -> response.setStatus(404));
        }
    }

    public void destroy() {
        super.destroy();
    }
}