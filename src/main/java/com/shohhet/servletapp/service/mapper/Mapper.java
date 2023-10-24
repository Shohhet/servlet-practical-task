package com.shohhet.servletapp.service.mapper;

public interface Mapper<F, T> {
    T mapFrom(F from);
}
