package com.shohhet.servletapp.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shohhet.servletapp.service.FileService;
import com.shohhet.servletapp.dto.GetFileRequestDto;
import com.shohhet.servletapp.dto.UploadFileRequestDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.shohhet.servletapp.utils.ServletUtils.isInteger;

@WebServlet(urlPatterns = {"/api/v1/files/*", "/api/v1/files"})
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100)
public class FileRestControllerV1 extends HttpServlet {
    private FileService fileService;
    private Gson gson;
    private static final String UPLOAD_DIR = "uploads";
    private static final String USER_ID_HEADER = "userId";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        fileService = (FileService) getServletContext().getAttribute(FileService.class.getSimpleName());
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        path = path == null || path.isEmpty() ? "" : path.substring(1);
        var writer = resp.getWriter();
        if (path.isEmpty()) {
            var fileDtos = fileService.getAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json; charset=UTF-8");
            writer.write(gson.toJson(fileDtos, new TypeToken<List<GetFileRequestDto>>() {
            }.getType()));
        } else if (Pattern.matches("^\\d+$", path) && isInteger(path)) {
            int id = Integer.parseInt(path);
            fileService.getById(id).ifPresentOrElse(
                    getFileRequestDto -> {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.setContentType("application/json; charset=UTF-8");
                        writer.write(gson.toJson(getFileRequestDto));
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writer.write("File not found.");
                    }
            );
        } else if (Pattern.matches("^\\d+/download$", path)) {
            String stringId = Arrays.stream(path.split("/")).findFirst().orElse("");
            if (isInteger(stringId)) {
                Integer id = Integer.parseInt(stringId);
                var maybeFile = fileService.getById(id);
                if (maybeFile.isPresent()) {
                    String fullPath = maybeFile.get().path() + File.separator + maybeFile.get().name();
                    File downloadFile = new File(fullPath);
                    resp.reset();
                    try (var inputStream = new FileInputStream(downloadFile);
                         var outputStream = resp.getOutputStream()) {
                        var context = getServletContext();
                        String mimeType = context.getMimeType(fullPath);
                        if (mimeType == null) {
                            mimeType = "application/octet-stream";
                        }
                        resp.setContentType(mimeType);
                        resp.setContentLength((int) downloadFile.length());
                        resp.setHeader(
                                "Content-Disposition",
                                String.format("attachment; filename=\"%s\"", downloadFile.getName())
                        );
                        byte[] buffer = new byte[4 * 1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writer.write("File not found.");
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write("Resource not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String stringUserId = req.getHeader(USER_ID_HEADER);
        PrintWriter writer = resp.getWriter();
        if (isInteger(stringUserId)) {
            String appPath = req.getServletContext().getRealPath("");
            String uploadDirectory = appPath + UPLOAD_DIR + File.separator + stringUserId;
            Part filePart = req.getPart("filename");
            int userId = Integer.parseInt(stringUserId);
            fileService.add(new UploadFileRequestDto(
                            filePart.getSubmittedFileName(),
                            uploadDirectory,
                            filePart.getInputStream(),
                            userId
                    )
            ).ifPresentOrElse(
                    getFileRequestDto -> {
                        resp.setStatus(HttpServletResponse.SC_CREATED);
                        resp.setContentType("application/json; charset=UTF-8");
                        writer.write(gson.toJson(getFileRequestDto));
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writer.write("File already exist.");
                    }
            );
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write("User id header missed or have wrong value");
        }
    }
}
