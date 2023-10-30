package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.FileEntity;
import com.shohhet.servletapp.service.dto.fileDto.UploadFileDto;

public class UploadDtoToFileMapper implements Mapper<UploadFileDto, FileEntity> {
    @Override
    public FileEntity mapFrom(UploadFileDto dto) {

        return FileEntity.builder()
                .name(dto.name())
                .path(dto.path())
                .build();
    }
}
