package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.FileEntity;
import com.shohhet.servletapp.service.dto.fileDto.FileDto;

public class FileToDtoMapper implements Mapper<FileEntity, FileDto> {

    @Override
    public FileDto mapFrom(FileEntity file) {
        return new FileDto(file.getId(), file.getName(), file.getPath());
    }
}
