package com.shohhet.servletapp.service;

import com.shohhet.servletapp.model.repository.impl.EventRepositoryImpl;
import com.shohhet.servletapp.service.dto.eventDto.EventDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventService {
    private final EventRepositoryImpl eventRepository;

}
