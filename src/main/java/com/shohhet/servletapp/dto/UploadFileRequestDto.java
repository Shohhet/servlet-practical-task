package com.shohhet.servletapp.dto;

import java.io.InputStream;

public record UploadFileRequestDto(String name, String path, InputStream fileInputStream, Integer userId) {
}
