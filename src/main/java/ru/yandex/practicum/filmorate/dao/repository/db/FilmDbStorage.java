package ru.yandex.practicum.filmorate.dao.repository.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("db")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String TABLE_NAME = "films";
    private static final String TABLE_MPA = "mpa_rating";
    private static final String TABLE_GENRE = "genre";
    private static final String TABLE_USER_FILM_LIKE = "user_film_like";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    public static final String SELECT_TOP_FILMS = """
            SELECT f.*, COUNT(fl.user_id) AS likes_count
            FROM films f
            LEFT JOIN user_film_like fl ON f.id = fl.film_id
            GROUP BY f.id
            ORDER BY likes_count DESC
            LIMIT ?
            """;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<Film> findAll() {
        return getAll(TABLE_NAME);
    }

    @Override
    public Film create(Film film) {
        long id = create(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRatingId()
        );
        film.setId(id);
        if (film.getGenre() != null && !film.getGenre().isEmpty()) {
            saveGenres(film.getId(), film.getGenre());
        }
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        update(
                UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpaRatingId(),
                newFilm.getId()
        );
        return newFilm;
    }

    @Override
    public Optional<Film> getEntity(long id) {
        return getById(TABLE_NAME, id);
    }

    @Override
    public boolean existsMpaById(long id) {
        return existsById(TABLE_MPA, id);
    }

    @Override
    public boolean existsGenreById(long id) {
        return existsById(TABLE_GENRE, id);
    }

    @Override
    public boolean isLikedByUser(Long id1, Long id2) {
        return existsRelation(TABLE_USER_FILM_LIKE, "user_id", id1, "film_id", id2);
    }

    @Override
    public void addLike(Long id1, Long id2) {
        addRelation(TABLE_USER_FILM_LIKE, "user_id", id1, "film_id", id2);
    }

    @Override
    public void removeLike(Long id1, Long id2) {
        removeRelation(TABLE_USER_FILM_LIKE, "user_id", id1, "film_id", id2);
    }

    @Override
    public Collection<Film> getTopFilms(int limit) {
        return jdbcTemplate.query(SELECT_TOP_FILMS, mapper, limit);
    }

    private void saveGenres(long filmId, List<Long> genres) {
        jdbcTemplate.batchUpdate(
                INSERT_FILM_GENRE,
                genres,
                genres.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                }
        );
    }
}
