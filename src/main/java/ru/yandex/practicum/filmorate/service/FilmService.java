package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public void addLike(Film film, User user) {
        if (film.getLikes().contains(user.getId())) {
            throw new ValidationException("Вы уже поставили лайк этому фильму");
        }
        film.getLikes().add(user.getId());
    }

    public void removeLike(Film film, User user) {
        if (!film.getLikes().contains(user.getId())) {
            throw new ValidationException("Вы еще не ставили лайк этому фильму, поэтому его нельзя удалить");
        }
        film.getLikes().remove(user.getId());
    }

    public Collection<Film> getTopFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
