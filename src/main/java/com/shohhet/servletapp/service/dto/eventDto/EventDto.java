package com.shohhet.servletapp.service.dto.eventDto;

public record EventDto(Integer id, com.shohhet.servletapp.service.dto.userDto.UserDto user, com.shohhet.servletapp.service.dto.fileDto.FileDto file) { }
