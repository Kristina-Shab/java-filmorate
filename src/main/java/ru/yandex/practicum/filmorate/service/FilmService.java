package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        log.info("Добавления лайка фильму {} пользователем {}", film.getName(), user.getLogin());
        if (film.getLikes().contains(user.getId())) {
            log.warn("Попытка добавить существующий лайк фильму {} пользователем {}", film.getName(), user.getLogin());
            throw new ValidationException("Вы уже поставили лайк этому фильму");
        }
        film.getLikes().add(user.getId());
        log.info("Лайк успешно добавлен фильму {} пользователем {}", film.getName(), user.getLogin());
        log.debug("Детали фильма после добавления лайка: {}", film);
        return film;
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        log.info("Удаление лайка фильму {} пользователем {}", film.getName(), user.getLogin());
        if (!film.getLikes().contains(user.getId())) {
            log.warn("Попытка удалить несуществующий лайк фильму {} пользователем {}", film.getName(), user.getLogin());
            throw new NotFoundException("Вы еще не ставили лайк этому фильму, поэтому его нельзя удалить");
        }
        film.getLikes().remove(user.getId());
        log.info("Лайк фильму {} от пользователя {} успешно удален", film.getName(), user.getLogin());
        log.debug("Детали фильма после удаления лайка: {}", film);
    }

    public Collection<Film> getTopFilms(int count) {
        log.info("Получение лучших {} фильмов", count);
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
