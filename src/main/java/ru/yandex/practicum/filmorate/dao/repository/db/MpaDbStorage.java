package ru.yandex.practicum.filmorate.dao.repository.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.MpaStorage;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("db")
public class MpaDbStorage extends BaseDbStorage<MpaRating> implements MpaStorage {
    private static final String TABLE_MPA = "mpa_rating";

    public MpaDbStorage(JdbcTemplate jdbcTemplate, RowMapper<MpaRating> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<MpaRating> findAll() {
        return getAll(TABLE_MPA);
    }

    @Override
    public Optional<MpaRating> findById(Long id) {
        return getById(TABLE_MPA, id);
    }
}

