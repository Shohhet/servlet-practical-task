package com.shohhet.servletapp.repository.impl;

import com.shohhet.servletapp.entity.UserEntity;
import org.hibernate.Session;

public class UserRepositoryImpl extends GenericRepositoryImpl<UserEntity, Integer> {
    public UserRepositoryImpl(Session session, Class<UserEntity> clazz) {
        super(session,clazz);
    }
}
