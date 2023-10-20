package com.shohhet.servletapp.model.repository.impl;

import com.shohhet.servletapp.model.entity.EventEntity;
import org.hibernate.Session;

public class EventRepositoryImpl extends GenericRepositoryImpl<EventEntity, Integer> {
    public EventRepositoryImpl(Session session, Class<EventEntity> clazz) {
        super(session, clazz);
    }
}
