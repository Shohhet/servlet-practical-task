package com.shohhet.servletapp.mapper;

public interface Mapper<F, T> {
    T mapFrom(F from);
}
