package com.lbycpd2.archieestimator.dao;

import java.util.List;

public interface DataAccessObjectInterface<T> {
    void save(T object);
    void update(int id, T object);
    void delete(int id);
    int getID(T object);
    T get(int id);
    T get(String string);
    List<T> getAll();
}
