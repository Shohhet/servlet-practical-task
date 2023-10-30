package com.shohhet.servletapp.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shohhet.servletapp.service.FileService;
import com.shohhet.servletapp.service.dto.fileDto.FileDto;
import com.shohhet.servletapp.service.dto.fileDto.UploadFileDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/api/users/*/files/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100)
public class FileServlet extends HttpServlet {
    private FileService fileService;
    private Gson gson;
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        fileService = (FileService) getServletContext().getAttribute(FileService.class.getSimpleName());
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        var writer = resp.getWriter();
        if (path != null && !path.isEmpty() && !path.isBlank()) {
            path = path.substring(1);
            if (path.isEmpty()) {
                var fileDtos = fileService.getAll();
                resp.setStatus(200);
                resp.setContentType("application/json; charset=UTF-8");
                writer.write(gson.toJson(fileDtos, new TypeToken<List<FileDto>>() {
                }.getType()));
            } else if (isInteger(path)) {
                int id = Integer.parseInt(path);
                fileService.getById(id).ifPresentOrElse(
                        fileDto -> {
                            resp.setStatus(200);
                            resp.setContentType("application/json; charset=UTF-8");
                            writer.write(gson.toJson(fileDto));
                        },
                        () -> resp.setStatus(404)
                );
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String appPath = req.getServletContext().getRealPath("");
        String uploadDirectory = appPath + UPLOAD_DIR;
        var filePart = req.getPart("filename");
        var writer = resp.getWriter();
        fileService.add(new UploadFileDto(
                filePart.getSubmittedFileName(),
                uploadDirectory,
                filePart.getInputStream()
                )
        ).ifPresentOrElse(
                fileDto -> {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.setContentType("application/json; charset=UTF-8");
                    writer.write(gson.toJson(fileDto));
                },
                () -> {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writer.write("File already exist.");
                }
        );


    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

    private boolean isInteger(String maybeInteger) {
        try {
            Integer.parseInt(maybeInteger);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
