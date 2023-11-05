package com.shohhet.servletapp.service.dto.eventDto;

import com.shohhet.servletapp.service.dto.fileDto.FileDto;
import com.shohhet.servletapp.service.dto.userDto.UserDto;

public record EventDto(Integer id, UserDto user, FileDto file) { }
