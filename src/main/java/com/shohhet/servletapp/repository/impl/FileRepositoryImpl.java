package com.shohhet.servletapp.repository.impl;

import com.shohhet.servletapp.entity.FileEntity;
import org.hibernate.Session;

public class FileRepositoryImpl extends GenericRepositoryImpl<FileEntity, Integer> {
    public FileRepositoryImpl(Session session, Class<FileEntity> clazz) {
        super(session, clazz);
    }
}
