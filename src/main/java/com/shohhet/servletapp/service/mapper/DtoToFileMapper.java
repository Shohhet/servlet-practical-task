package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.FileEntity;
import com.shohhet.servletapp.service.dto.fileDto.FileDto;

public class DtoToFileMapper implements Mapper<FileDto, FileEntity> {
    @Override
    public FileEntity mapFrom(FileDto dto) {

        return FileEntity.builder()
                .id(dto.id())
                .name(dto.name())
                .path(dto.path())
                .build();
    }
}
