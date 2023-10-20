package com.shohhet.servletapp.model.repository.impl;

import com.shohhet.servletapp.model.entity.FileEntity;
import org.hibernate.Session;

public class FileRepositoryImpl extends GenericRepositoryImpl<FileEntity, Integer> {
    public FileRepositoryImpl(Session session, Class<FileEntity> clazz) {
        super(session, clazz);
    }
}
