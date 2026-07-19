package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.repository.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("db")
@JdbcTest
@Import({
        GenreDbStorage.class,
        GenreRowMapper.class
})
@AutoConfigureTestDatabase
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    void testFindAll() {
        Collection<Genre> all = genreStorage.findAll();

        assertThat(all).hasSize(6);
        assertThat(all).extracting(Genre::getId).containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 6L);
        assertThat(all).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    void testFindById() {
        Optional<Genre> found = genreStorage.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
        assertThat(found.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void testFindByIdLast() {
        Optional<Genre> found = genreStorage.findById(6L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(6L);
        assertThat(found.get().getName()).isEqualTo("Боевик");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Genre> found = genreStorage.findById(999L);

        assertThat(found).isEmpty();
    }
}
