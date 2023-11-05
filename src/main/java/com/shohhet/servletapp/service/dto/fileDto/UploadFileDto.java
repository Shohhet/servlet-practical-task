package com.shohhet.servletapp.service.dto.fileDto;

import jakarta.servlet.http.Part;

import java.io.InputStream;

public record UploadFileDto(String name, String path, InputStream fileInputStream, Integer userId) {
}
