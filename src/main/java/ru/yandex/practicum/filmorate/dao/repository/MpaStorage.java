package ru.yandex.practicum.filmorate.dao.repository;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Collection<MpaRating> findAll();

    Optional<MpaRating> findById(Long id);
}
