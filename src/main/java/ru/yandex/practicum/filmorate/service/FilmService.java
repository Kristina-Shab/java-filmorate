package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;
import ru.yandex.practicum.filmorate.dto.request.FilmCreatRequestDto;
import ru.yandex.practicum.filmorate.dto.request.FilmUpdateRequestDto;
import ru.yandex.practicum.filmorate.dto.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(@Qualifier("db") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<FilmResponseDto> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmResponseDto)
                .toList();
    }

    public FilmResponseDto create(FilmCreatRequestDto request) {
        log.info("Добавления нового фильма {}", request.getName());
        checkGenres(request);
        checkMpa(request);

        Film film = FilmMapper.mapToFilm(request);
        Film savedFilm = filmStorage.create(film);
        log.info("Фильм успешно добавлен, id= {}", savedFilm.getId());
        return FilmMapper.mapToFilmResponseDto(savedFilm);
    }

    public FilmResponseDto update(FilmUpdateRequestDto request) {
        if (request.getId() == null) {
            log.warn("Не указан id для обновления");
            throw new ValidationException("Id должен быть указан");
        }
        Film updatedFilm = filmStorage.getEntity(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм с id " + request.getId() + " не найден"));
        Film savedFilm = filmStorage.update(updatedFilm);
        return FilmMapper.mapToFilmResponseDto(savedFilm);
    }

    public FilmResponseDto addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        log.info("Добавления лайка фильму {} пользователем {}", film.getName(), user.getLogin());
        if (filmStorage.isLikedByUser(userId, filmId)) {
            log.warn("Попытка добавить существующий лайк фильму {} пользователем {}", film.getName(), user.getLogin());
            throw new ValidationException("Вы уже поставили лайк этому фильму");
        }
        filmStorage.addLike(userId, filmId);
        log.info("Лайк успешно добавлен фильму {} пользователем {}", film.getName(), user.getLogin());
        log.debug("Детали фильма после добавления лайка: {}", film);
        return FilmMapper.mapToFilmResponseDto(film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        log.info("Удаление лайка фильму {} пользователем {}", film.getName(), user.getLogin());
        if (!filmStorage.isLikedByUser(userId, filmId)) {
            log.warn("Попытка удалить несуществующий лайк фильму {} пользователем {}", film.getName(), user.getLogin());
            throw new NotFoundException("Вы еще не ставили лайк этому фильму, поэтому его нельзя удалить");
        }
        filmStorage.removeLike(userId, filmId);
        log.info("Лайк фильму {} от пользователя {} успешно удален", film.getName(), user.getLogin());
        log.debug("Детали фильма после удаления лайка: {}", film);
    }

    public Collection<FilmResponseDto> getTopFilms(int count) {
        log.info("Получение лучших {} фильмов", count);
        return filmStorage.getTopFilms(count).stream()
                .map(FilmMapper::mapToFilmResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<FilmResponseDto> findById(Long id) {
        return filmStorage.getEntity(id)
                .map(FilmMapper::mapToFilmResponseDto);
    }

    private Film getFilmById(Long id) {
        return filmStorage.getEntity(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    private void checkMpa(FilmCreatRequestDto request) {
        if (request.getMpa() == null) {
            return;
        }

        boolean existsMpa = filmStorage.existsMpaById(request.getMpa().getId());
        if (!existsMpa) {
            throw new NotFoundException("Рейтинг с id=" + request.getMpa().getId() + " не найден");
        }
    }

    private void checkGenres(FilmCreatRequestDto request) {
        if (request.getGenres() == null || request.getGenres().isEmpty()) {
            return;
        }

        for (GenreRequestDto genre : request.getGenres()) {
            boolean exists = filmStorage.existsGenreById(genre.getId());
            if (!exists) {
                throw new NotFoundException("Жанр с id=" +  genre.getId() + " не найден");
            }
        }
    }
}
