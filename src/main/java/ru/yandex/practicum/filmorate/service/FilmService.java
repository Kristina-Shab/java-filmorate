package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (film.getLikes().contains(user.getId())) {
            throw new ValidationException("Вы уже поставили лайк этому фильму");
        }
        film.getLikes().add(user.getId());
        return film;
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (!film.getLikes().contains(user.getId())) {
            throw new NotFoundException("Вы еще не ставили лайк этому фильму, поэтому его нельзя удалить");
        }
        film.getLikes().remove(user.getId());
    }

    public Collection<Film> getTopFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    public Optional<Film> findById (Long id){
        return filmStorage.getFilm(id);
    }

    private Film getFilmById(Long id){
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }
}
