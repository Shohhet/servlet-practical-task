package com.shohhet.servletapp.model.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<E, ID> {
    E add(E entity);
    Optional<E> getById(ID id);
    List<E> getAll();
    void update(E entity);
    void delete(ID id);

}
