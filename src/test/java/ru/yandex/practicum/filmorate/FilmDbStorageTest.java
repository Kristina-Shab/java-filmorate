package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.repository.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("db")
@JdbcTest
@Import({
        FilmDbStorage.class,
        FilmRowMapper.class,
        GenreRowMapper.class
})
@AutoConfigureTestDatabase
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    private Film createTestFilm() {
        return Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(MpaRating.builder().id(1L).build())
                .build();
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM film_genre");
        jdbcTemplate.execute("DELETE FROM user_film_like");
        jdbcTemplate.execute("DELETE FROM user_friend");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void testCreate() {
        Film film = createTestFilm();
        Film created = filmStorage.create(film);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Film");
        assertThat(created.getDescription()).isEqualTo("Test Description");
        assertThat(created.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(created.getDuration()).isEqualTo(120);
        assertThat(created.getMpa().getId()).isEqualTo(1L);
    }

    @Test
    void testCreateWithGenres() {
        Film film = createTestFilm().toBuilder()
                .genres(List.of(Genre.builder().id(1L).build(), Genre.builder().id(2L).build()))
                .build();
        Film created = filmStorage.create(film);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getGenres())
                .extracting(Genre::getId)
                .containsExactly(1L, 2L);

        List<Long> genreIds = jdbcTemplate.queryForList(
                "SELECT genre_id FROM film_genre WHERE film_id = ? ORDER BY genre_id",
                Long.class, created.getId());
        assertThat(genreIds).containsExactly(1L, 2L);
    }

    @Test
    void testFindAll() {
        Film created = filmStorage.create(createTestFilm());

        Collection<Film> all = filmStorage.findAll();

        assertThat(all).isNotEmpty();
        assertThat(all).extracting(Film::getId).contains(created.getId());
    }

    @Test
    void testGetEntity() {
        Film created = filmStorage.create(createTestFilm());

        Optional<Film> found = filmStorage.getEntity(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(created.getId());
        assertThat(found.get().getName()).isEqualTo("Test Film");
    }

    @Test
    void testGetEntityNotFound() {
        Optional<Film> found = filmStorage.getEntity(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void testUpdate() {
        Film created = filmStorage.create(createTestFilm());

        Film updatedFilm = created.toBuilder()
                .name("Updated Name")
                .description("Updated Description")
                .duration(150)
                .build();

        filmStorage.update(updatedFilm);

        Optional<Film> found = filmStorage.getEntity(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Name");
        assertThat(found.get().getDescription()).isEqualTo("Updated Description");
        assertThat(found.get().getDuration()).isEqualTo(150);
    }

    @Test
    void testExistsMpaById() {
        assertThat(filmStorage.existsMpaById(1L)).isTrue();
        assertThat(filmStorage.existsMpaById(999L)).isFalse();
    }

    @Test
    void testExistsGenreById() {
        assertThat(filmStorage.existsGenreById(1L)).isTrue();
        assertThat(filmStorage.existsGenreById(999L)).isFalse();
    }

    @Test
    void testAddLike() {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@test.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));
        Film created = filmStorage.create(createTestFilm());

        filmStorage.addLike(1L, created.getId());

        assertThat(filmStorage.isLikedByUser(1L, created.getId())).isTrue();
    }

    @Test
    void testRemoveLike() {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@test.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));
        Film created = filmStorage.create(createTestFilm());
        filmStorage.addLike(1L, created.getId());

        filmStorage.removeLike(1L, created.getId());

        assertThat(filmStorage.isLikedByUser(1L, created.getId())).isFalse();
    }

    @Test
    void testIsLikedByUser() {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@test.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1));
        Film created = filmStorage.create(createTestFilm());

        assertThat(filmStorage.isLikedByUser(1L, created.getId())).isFalse();

        filmStorage.addLike(1L, created.getId());

        assertThat(filmStorage.isLikedByUser(1L, created.getId())).isTrue();
    }

    @Test
    void testGetTopFilms() {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user1@test.com", "user1", "User 1", LocalDate.of(1990, 1, 1));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user2@test.com", "user2", "User 2", LocalDate.of(1990, 1, 1));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user3@test.com", "user3", "User 3", LocalDate.of(1990, 1, 1));

        Film film1 = filmStorage.create(createTestFilm().toBuilder().name("Film A").build());
        Film film2 = filmStorage.create(createTestFilm().toBuilder().name("Film B").build());
        Film film3 = filmStorage.create(createTestFilm().toBuilder().name("Film C").build());

        filmStorage.addLike(1L, film1.getId());
        filmStorage.addLike(2L, film1.getId());
        filmStorage.addLike(3L, film1.getId());
        filmStorage.addLike(1L, film2.getId());

        Collection<Film> top = filmStorage.getTopFilms(2);

        assertThat(top).hasSize(2);
        assertThat(top).extracting(Film::getId)
                .containsExactly(film1.getId(), film2.getId());
    }

    @Test
    void testUpdateNonExistentFilm() {
        Film film = createTestFilm().toBuilder().id(999L).build();

        assertThrows(DbException.class, () -> filmStorage.update(film));
    }
}
