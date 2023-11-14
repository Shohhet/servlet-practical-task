package com.shohhet.servletapp.mapper;

import com.shohhet.servletapp.entity.FileEntity;
import com.shohhet.servletapp.dto.UploadFileRequestDto;

public class UploadDtoToFileMapper implements Mapper<UploadFileRequestDto, FileEntity> {
    @Override
    public FileEntity mapFrom(UploadFileRequestDto dto) {

        return FileEntity.builder()
                .name(dto.name())
                .path(dto.path())
                .build();
    }
}
