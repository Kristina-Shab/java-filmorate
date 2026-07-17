package ru.yandex.practicum.filmorate.dao.repository.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("db")
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    private static final String TABLE_GENRE = "genre";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genre> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return getAll(TABLE_GENRE);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return getById(TABLE_GENRE, id);
    }
}
