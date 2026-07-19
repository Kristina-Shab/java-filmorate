package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.dao.repository.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("db")
@JdbcTest
@Import({
        MpaDbStorage.class,
        MpaRatingRowMapper.class
})
@AutoConfigureTestDatabase
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    void testFindAll() {
        Collection<MpaRating> all = mpaStorage.findAll();

        assertThat(all).hasSize(5);
        assertThat(all).extracting(MpaRating::getId).containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L);
        assertThat(all).extracting(MpaRating::getName)
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void testFindById() {
        Optional<MpaRating> found = mpaStorage.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
        assertThat(found.get().getName()).isEqualTo("G");
    }

    @Test
    void testFindByIdLast() {
        Optional<MpaRating> found = mpaStorage.findById(5L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(5L);
        assertThat(found.get().getName()).isEqualTo("NC-17");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<MpaRating> found = mpaStorage.findById(999L);

        assertThat(found).isEmpty();
    }
}
