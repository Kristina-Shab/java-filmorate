package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTests {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
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
        assertThrows(ValidationException.class, () -> filmController.update(film));
    }

    @Test
    void updateInvalidFilmIsBlank() {
        Film newEmptyFilm = Film.builder().build();

        assertThrows(Exception.class, () -> filmController.update(newEmptyFilm));
    }

    private Film validFilm() {
        return Film.builder()
                .name("Film")
                .description("A movie about friends")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(207)
                .build();
    }
}
