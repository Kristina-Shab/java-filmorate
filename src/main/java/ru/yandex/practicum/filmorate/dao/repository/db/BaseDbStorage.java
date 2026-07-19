package ru.yandex.practicum.filmorate.dao.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.DbException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> mapper;

    protected Optional<T> getById(String tableName, Long id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, mapper, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected boolean existsById(String tableName, Long id) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    protected Collection<T> getAll(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        return jdbcTemplate.query(sql, mapper);
    }

    protected long create(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            return id;
        } else {
            throw new DbException("Не удалось сохранить данные");
        }
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(query, params);
        if (rowsUpdated == 0) {
            throw new DbException("Не удалось обновить данные");
        }
    }

    protected boolean existsRelation(String tableName, String column1, Long value1, String column2, Long value2) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND %s = ?", tableName, column1, column2);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, value1, value2);
        return count != null && count > 0;
    }

    protected void addRelation(String tableName, String column1, Long value1, String column2, Long value2) {
        String sql = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)", tableName, column1, column2);
        jdbcTemplate.update(sql, value1, value2);
    }

    protected void removeRelation(String tableName, String column1, Long value1, String column2, Long value2) {
        String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", tableName, column1, column2);
        jdbcTemplate.update(sql, value1, value2);
    }

    protected Collection<T> getRelations(String relationTableName, String sourceColumn, Long sourceId,
                                         String targetTableName, String targetColumn, String joinColumn) {
        String sql = String.format("""
                SELECT t.*
                FROM %s t
                JOIN %s r ON t.%s = r.%s
                WHERE r.%s = ?
                ORDER BY t.id
                """, targetTableName, relationTableName, targetColumn, joinColumn, sourceColumn);

        return jdbcTemplate.query(sql, mapper, sourceId);
    }
}