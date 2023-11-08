package com.shohhet.servletapp.service.dto.eventDto;

import com.shohhet.servletapp.model.entity.FileEntity;
import com.shohhet.servletapp.model.entity.UserEntity;

public record EventDto(Integer id, UserEntity user, FileEntity file) { }
