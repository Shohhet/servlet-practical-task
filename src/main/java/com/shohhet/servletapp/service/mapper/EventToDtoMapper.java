package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.EventEntity;
import com.shohhet.servletapp.service.dto.eventDto.EventDto;

public class EventToDtoMapper implements Mapper<EventEntity, EventDto>{
    @Override
    public EventDto mapFrom(EventEntity entity) {
        return new EventDto(entity.getId(), entity.getUser(), entity.getFile());
    }
}
