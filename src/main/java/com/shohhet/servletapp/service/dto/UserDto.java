package com.shohhet.servletapp.service.dto;

import java.util.List;

public record UserDto(Integer id, String name, List<EventDto> events) {
}
