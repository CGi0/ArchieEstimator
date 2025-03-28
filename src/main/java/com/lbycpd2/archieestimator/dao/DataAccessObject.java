package com.lbycpd2.archieestimator.dao;

public interface DataAccessObject<T> {
    void save(T object);
    void update(T object);
    void delete(T object);
}
