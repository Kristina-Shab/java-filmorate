package ru.yandex.practicum.filmorate.dao.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage extends BaseStorage<Film> {
    boolean isLikedByUser(Long id1, Long id2);

    void addLike(Long id1, Long id2);

    void removeLike(Long id1, Long id2);

    Collection<Film> getTopFilms(int limit);

    boolean existsMpaById(long id);

    boolean existsGenreById(long id);
}
