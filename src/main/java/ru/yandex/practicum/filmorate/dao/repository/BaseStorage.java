package ru.yandex.practicum.filmorate.dao.repository;

import java.util.Collection;
import java.util.Optional;

public interface BaseStorage<T> {
    Collection<T> findAll();

    T create(T entity);

    T update(T newEntity);

    Optional<T> getEntity(Long id);
}
