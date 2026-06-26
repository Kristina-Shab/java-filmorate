package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTests {
    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        FilmService filmService = new FilmService(filmStorage, userService);
        filmController = new FilmController(filmService);
        userController = new UserController(userService);

    }

    @Test
    void createValidFilm() {
        Film film = validFilm();
        Film createdFilm = filmController.create(film);

        assertNotNull(createdFilm.getId());
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    void createMultipleValidFilms() {
        Film film = validFilm();
        Film createdFilm1 = filmController.create(film);
        Film createdFilm2 = filmController.create(film);
        Film createdFilm3 = filmController.create(film);

        assertNotNull(createdFilm1.getId());
        assertNotNull(createdFilm2.getId());
        assertNotNull(createdFilm3.getId());
        assertEquals(3, filmController.findAll().size());
    }

    @Test
    void updateValidFilm() {
        Film film = validFilm();
        Film createdFilm = filmController.create(film);
        Film newFilm = validFilm().toBuilder()
                .id(createdFilm.getId())
                .name("Apple")
                .build();
        Film updatedFilm = filmController.update(newFilm);

        assertEquals(1, filmController.findAll().size());
        assertEquals(newFilm.getName(), updatedFilm.getName());
    }

    @Test
    void updateInvalidFilmIdDoesNotExist() {
        Film film = validFilm().toBuilder()
                .id(5L)
                .build();
        assertThrows(NotFoundException.class, () -> filmController.update(film));
    }

    @Test
    void updateInvalidFilmIsBlank() {
        Film newEmptyFilm = Film.builder().build();

        assertThrows(Exception.class, () -> filmController.update(newEmptyFilm));
    }

    @Test
    void addLikeValid() {
        Film film = validFilm();
        User user = validUser();
        Film createdFilm = filmController.create(film);
        User createdUser = userController.create(user);

        filmController.addLike(createdFilm.getId(), createdUser.getId());

        assertEquals(1, createdFilm.getLikes().size());
    }

    @Test
    void addLikeInvalidWhenUserAlreadyLikedFilm() {
        Film film = validFilm();
        User user = validUser();
        Film createdFilm = filmController.create(film);
        User createdUser = userController.create(user);

        filmController.addLike(film.getId(), user.getId());

        assertThrows(ValidationException.class, () -> filmController.addLike(createdFilm.getId(), createdUser.getId()));
    }

    @Test
    void addLikeMultipleValid() {
        Film film = validFilm();
        User user1 = validUser();
        User user2 = validUser();
        User user3 = validUser();
        Film createdFilm = filmController.create(film);
        User createdUser1 = userController.create(user1);
        User createdUser2 = userController.create(user2);
        User createdUser3 = userController.create(user3);

        filmController.addLike(createdFilm.getId(), createdUser1.getId());
        filmController.addLike(createdFilm.getId(), createdUser2.getId());
        filmController.addLike(createdFilm.getId(), createdUser3.getId());

        assertEquals(3, createdFilm.getLikes().size());
    }

    @Test
    void removeLikeValid() {
        Film film = validFilm();
        User user = validUser();
        Film createdFilm = filmController.create(film);
        User createdUser = userController.create(user);

        filmController.addLike(createdFilm.getId(), createdUser.getId());
        filmController.removeLike(createdFilm.getId(), createdUser.getId());

        assertEquals(0, createdFilm.getLikes().size());
    }

    private Film validFilm() {
        return Film.builder()
                .name("Film")
                .description("A movie about friends")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(207)
                .build();
    }

    private User validUser() {
        return User.builder()
                .login("Nik")
                .email("niki@mail.ru")
                .name("Никита")
                .birthday(LocalDate.of(2020, 11, 25))
                .build();
    }
}
