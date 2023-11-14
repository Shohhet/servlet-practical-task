package com.shohhet.servletapp.repository.impl;

import com.shohhet.servletapp.repository.GenericRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class GenericRepositoryImpl<E, ID> implements GenericRepository<E, ID> {

    private final Session session;
    private final Class<E> clazz;

    @Override
    public E add(E entity) {
        session.persist(entity);
        return entity;
    }

    @Override
    public Optional<E> getById(ID id) {
        return Optional.ofNullable(session.find(clazz, id));
    }

    @Override
    public List<E> getAll() {
        var criteria = session.getCriteriaBuilder().createQuery(clazz);
        criteria.from(clazz);
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public void update(E entity) {
        session.merge(entity);
    }

    @Override
    public void delete(E entity) {
        session.remove(entity);
    }
}
