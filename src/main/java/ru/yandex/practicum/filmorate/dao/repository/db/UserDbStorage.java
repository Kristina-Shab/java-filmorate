package ru.yandex.practicum.filmorate.dao.repository.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.repository.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("db")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_FRIEND = "user_friend";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<User> findAll() {
        return getAll(TABLE_USER);
    }

    @Override
    public User create(User user) {
        long id = create(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User newUser) {
        update(
                UPDATE_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public Optional<User> getEntity(long id) {
        return getById(TABLE_USER, id);
    }

    @Override
    public boolean areFriends(Long userId, Long friendId) {
        return existsRelation(TABLE_USER_FRIEND, "user_id", userId, "friend_id", friendId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        addRelation(TABLE_USER_FRIEND, "user_id", userId, "friend_id", friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        removeRelation(TABLE_USER_FRIEND, "user_id", userId, "friend_id", friendId);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        return getRelations(TABLE_USER_FRIEND, "user_id", id, TABLE_USER, "id", "friend_id");
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        String sql = """
                
                    SELECT u.*
                FROM users u
                INNER JOIN user_friend uf1 ON u.id = uf1.friend_id AND uf1.user_id = ?
                INNER JOIN user_friend uf2 ON u.id = uf2.friend_id AND uf2.user_id = ?
                ORDER BY u.id
                """;
        return jdbcTemplate.query(sql, mapper, userId,
                otherUserId);
    }
}
