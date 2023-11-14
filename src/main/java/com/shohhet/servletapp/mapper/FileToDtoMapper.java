package com.shohhet.servletapp.mapper;

import com.shohhet.servletapp.entity.FileEntity;
import com.shohhet.servletapp.dto.GetFileRequestDto;

public class FileToDtoMapper implements Mapper<FileEntity, GetFileRequestDto> {

    @Override
    public GetFileRequestDto mapFrom(FileEntity file) {
        return new GetFileRequestDto(file.getId(), file.getName(), file.getPath());
    }
}
