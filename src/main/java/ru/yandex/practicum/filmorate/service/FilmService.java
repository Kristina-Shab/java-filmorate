package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;
import ru.yandex.practicum.filmorate.dao.repository.GenreStorage;
import ru.yandex.practicum.filmorate.dao.repository.MpaStorage;
import ru.yandex.practicum.filmorate.dto.request.FilmCreateRequestDto;
import ru.yandex.practicum.filmorate.dto.request.FilmUpdateRequestDto;
import ru.yandex.practicum.filmorate.dto.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(
            @Qualifier("db") FilmStorage filmStorage,
            UserService userService,
            MpaStorage mpaStorage,
            GenreStorage genreStorage
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<FilmResponseDto> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmResponseDto)
                .toList();
    }

    public FilmResponseDto create(FilmCreateRequestDto request) {
        log.info("Добавления нового фильма {}", request.getName());
        checkGenres(request);
        checkMpa(request);

        Film film = FilmMapper.mapToFilm(request);
        Film savedFilm = filmStorage.create(film);

        if (savedFilm.getMpa() != null && savedFilm.getMpa().getId() != null) {
            MpaRating fullMpa = mpaStorage.findById(savedFilm.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг с id " + savedFilm.getMpa().getId() + " не найден"));
            savedFilm.setMpa(fullMpa);
        }

        if (savedFilm.getGenres() != null && !savedFilm.getGenres().isEmpty()) {
            List<Genre> fullGenres = new ArrayList<>();
            for (Genre genre : savedFilm.getGenres()) {
                Genre fullGenre = genreStorage.findById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id " + genre.getId() + " не найден"));
                fullGenres.add(fullGenre);
            }
            savedFilm.setGenres(fullGenres);
        }

        log.info("Фильм успешно добавлен, id= {}", savedFilm.getId());
        return FilmMapper.mapToFilmResponseDto(savedFilm);

    }

    public FilmResponseDto update(FilmUpdateRequestDto request) {
        Film updatedFilm = filmStorage.getEntity(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм с id " + request.getId() + " не найден"));
        Film savedFilm = filmStorage.update(updatedFilm);

        if (savedFilm.getMpa() != null && savedFilm.getMpa().getId() != null) {
            MpaRating fullMpa = mpaStorage.findById(savedFilm.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг c id= " + savedFilm.getMpa().getId() + " не найден"));
            savedFilm.setMpa(fullMpa);
        }

        if (savedFilm.getGenres() != null && !savedFilm.getGenres().isEmpty()) {
            List<Genre> fullGenres = new ArrayList<>();
            for (Genre genre : savedFilm.getGenres()) {
                Genre fullGenre = genreStorage.findById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id " + genre.getId() + " не найден"));
                fullGenres.add(fullGenre);
            }
            savedFilm.setGenres(fullGenres);
        }
        return FilmMapper.mapToFilmResponseDto(savedFilm);
    }

    public FilmResponseDto addLike(Long filmId, Long userId) {
        log.info("Добавления лайка фильму c id={} пользователем c id={}", filmId, userId);
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (filmStorage.isLikedByUser(userId, filmId)) {
            log.warn("Неуспешная попытка добавить существующий лайк фильму {} пользователем {}",
                    film.getName(), user.getLogin());
            throw new ValidationException("Вы уже поставили лайк этому фильму");
        }
        filmStorage.addLike(userId, filmId);
        log.info("Лайк успешно добавлен фильму {} пользователем {}", film.getName(), user.getLogin());
        log.debug("Детали фильма после добавления лайка: {}", film);
        return FilmMapper.mapToFilmResponseDto(film);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка фильму c id={} пользователем c id={}", filmId, userId);
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (!filmStorage.isLikedByUser(userId, filmId)) {
            log.warn("Неуспешная попытка удалить несуществующий лайк фильму {} пользователем {}",
                    film.getName(), user.getLogin());
            throw new NotFoundException(
                    "Вы еще не ставили лайк этому фильму с id = %s, поэтому его нельзя удалить".formatted(filmId)
            );
        }
        filmStorage.removeLike(userId, filmId);
        log.info("Лайк фильму {} от пользователя {} успешно удален", film.getName(), user.getLogin());
        log.debug("Детали фильма после удаления лайка: {}", film);
    }

    public Collection<FilmResponseDto> getTopFilms(int count) {
        log.info("Получение лучших {} фильмов", count);
        Collection<Film> films = filmStorage.getTopFilms(count);
        if (films == null) {
            return Collections.emptyList();
        }
        return films.stream()
                .map(FilmMapper::mapToFilmResponseDto)
                .toList();
    }

    public Optional<FilmResponseDto> findById(Long id) {
        return filmStorage.getEntity(id)
                .map(film -> {
                    if (film.getMpa() != null && film.getMpa().getId() != null) {
                        mpaStorage.findById(film.getMpa().getId())
                                .ifPresent(film::setMpa);
                    }
                    List<Genre> genres = filmStorage.findGenresByFilmId(id);
                    film.setGenres(genres);
                    return FilmMapper.mapToFilmResponseDto(film);
                });
    }

    private Film getFilmById(Long id) {
        return filmStorage.getEntity(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    private void checkMpa(FilmCreateRequestDto request) {
        if (request.getMpa() == null) {
            return;
        }

        boolean existsMpa = filmStorage.existsMpaById(request.getMpa().getId());
        if (!existsMpa) {
            throw new NotFoundException("Рейтинг с id=" + request.getMpa().getId() + " не найден");
        }
    }

    private void checkGenres(FilmCreateRequestDto request) {
        if (request.getGenres() == null || request.getGenres().isEmpty()) {
            return;
        }

        for (GenreRequestDto genre : request.getGenres()) {
            boolean exists = filmStorage.existsGenreById(genre.getId());
            if (!exists) {
                throw new NotFoundException("Жанр с id=" + genre.getId() + " не найден");
            }
        }
    }
}

