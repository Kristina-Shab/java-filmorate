package ru.yandex.practicum.filmorate.dao.repository.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

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
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String FIND_GENRES_BY_FILM_ID = """
            SELECT g.* FROM genre g
            JOIN film_genre fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            ORDER BY g.id
            """;
    private static final String SELECT_TOP_FILMS = """
            SELECT f.*, COUNT(fl.user_id) AS likes_count
            FROM films f
            LEFT JOIN user_film_like fl ON f.id = fl.film_id
            GROUP BY f.id
            ORDER BY likes_count DESC
            LIMIT ?
            """;

    private final RowMapper<Genre> genreRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> mapper, RowMapper<Genre> genreRowMapper) {
        super(jdbcTemplate, mapper);
        this.genreRowMapper = genreRowMapper;
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
                film.getMpa().getId()
        );
        film.setId(id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Long> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .toList();
            saveGenres(film.getId(), genreIds);
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
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        return newFilm;
    }

    @Override
    public Optional<Film> getEntity(Long id) {
        return getById(TABLE_NAME, id);
    }

    @Override
    public List<Genre> findGenresByFilmId(Long filmId) {
        return jdbcTemplate.query(FIND_GENRES_BY_FILM_ID, genreRowMapper, filmId);
    }

    @Override
    public boolean existsMpaById(Long id) {
        return existsById(TABLE_MPA, id);
    }

    @Override
    public boolean existsGenreById(Long id) {
        return existsById(TABLE_GENRE, id);
    }

    @Override
    public boolean isLikedByUser(Long userId, Long filmId) {
        return existsRelation(TABLE_USER_FILM_LIKE, "user_id", userId, "film_id", filmId);
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        addRelation(TABLE_USER_FILM_LIKE, "user_id", userId, "film_id", filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        removeRelation(TABLE_USER_FILM_LIKE, "user_id", userId, "film_id", filmId);
    }

    @Override
    public Collection<Film> getTopFilms(int limit) {
        return jdbcTemplate.query(SELECT_TOP_FILMS, mapper, limit);
    }

    private void saveGenres(Long filmId, List<Long> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        List<Long> uniqueGenres = genres.stream()
                .distinct()
                .toList();

        jdbcTemplate.batchUpdate(
                INSERT_FILM_GENRE,
                uniqueGenres,
                uniqueGenres.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                }
        );
    }
}
