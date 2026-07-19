package ru.yandex.practicum.filmorate.dao.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface FilmStorage extends BaseStorage<Film> {
    boolean isLikedByUser(Long userId, Long filmId);

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    Collection<Film> getTopFilms(int limit);

    boolean existsMpaById(Long id);

    boolean existsGenreById(Long id);

    List<Genre> findGenresByFilmId(Long filmId);
}
