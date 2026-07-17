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
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dao.repository.db.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("db")
@JdbcTest
@Import({UserDbStorage.class, UserRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    private User createTestUser() {
        return User.builder()
                .email("test@test.com")
                .login("testlogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM user_film_like");
        jdbcTemplate.execute("DELETE FROM user_friend");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void testCreate() {
        User user = createTestUser();
        User created = userStorage.create(user);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("test@test.com");
        assertThat(created.getLogin()).isEqualTo("testlogin");
        assertThat(created.getName()).isEqualTo("Test User");
        assertThat(created.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testFindAll() {
        User created = userStorage.create(createTestUser());

        Collection<User> all = userStorage.findAll();

        assertThat(all).isNotEmpty();
        assertThat(all).extracting(User::getId).contains(created.getId());
    }

    @Test
    void testGetEntity() {
        User created = userStorage.create(createTestUser());

        Optional<User> found = userStorage.getEntity(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(created.getId());
        assertThat(found.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void testGetEntityNotFound() {
        Optional<User> found = userStorage.getEntity(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void testUpdate() {
        User created = userStorage.create(createTestUser());

        User updatedUser = created.toBuilder()
                .email("updated@test.com")
                .login("updatedlogin")
                .name("Updated User")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        userStorage.update(updatedUser);

        Optional<User> found = userStorage.getEntity(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("updated@test.com");
        assertThat(found.get().getLogin()).isEqualTo("updatedlogin");
        assertThat(found.get().getName()).isEqualTo("Updated User");
        assertThat(found.get().getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));
    }

    @Test
    void testUpdateNonExistentUser() {
        User user = createTestUser().toBuilder().id(999L).build();

        assertThrows(DbException.class, () -> userStorage.update(user));
    }

    @Test
    void testAddFriend() {
        User user1 = userStorage.create(createTestUser().toBuilder()
                .email("user1@test.com").login("user1").build());
        User user2 = userStorage.create(createTestUser().toBuilder()
                .email("user2@test.com").login("user2").build());

        userStorage.addFriend(user1.getId(), user2.getId());

        assertThat(userStorage.areFriends(user1.getId(), user2.getId())).isTrue();
    }

    @Test
    void testRemoveFriend() {
        User user1 = userStorage.create(createTestUser().toBuilder()
                .email("user1@test.com").login("user1").build());
        User user2 = userStorage.create(createTestUser().toBuilder()
                .email("user2@test.com").login("user2").build());
        userStorage.addFriend(user1.getId(), user2.getId());

        userStorage.removeFriend(user1.getId(), user2.getId());

        assertThat(userStorage.areFriends(user1.getId(), user2.getId())).isFalse();
    }

    @Test
    void testAreFriends() {
        User user1 = userStorage.create(createTestUser().toBuilder()
                .email("user1@test.com").login("user1").build());
        User user2 = userStorage.create(createTestUser().toBuilder()
                .email("user2@test.com").login("user2").build());

        assertThat(userStorage.areFriends(user1.getId(), user2.getId())).isFalse();

        userStorage.addFriend(user1.getId(), user2.getId());

        assertThat(userStorage.areFriends(user1.getId(), user2.getId())).isTrue();
    }

    @Test
    void testGetFriends() {
        User user1 = userStorage.create(createTestUser().toBuilder()
                .email("user1@test.com").login("user1").build());
        User user2 = userStorage.create(createTestUser().toBuilder()
                .email("user2@test.com").login("user2").build());
        User user3 = userStorage.create(createTestUser().toBuilder()
                .email("user3@test.com").login("user3").build());

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user1.getId(), user3.getId());

        Collection<User> friends = userStorage.getFriends(user1.getId());

        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(User::getId)
                .containsExactlyInAnyOrder(user2.getId(), user3.getId());
    }

    @Test
    void testGetCommonFriends() {
        User user1 = userStorage.create(createTestUser().toBuilder()
                .email("user1@test.com").login("user1").build());
        User user2 = userStorage.create(createTestUser().toBuilder()
                .email("user2@test.com").login("user2").build());
        User common = userStorage.create(createTestUser().toBuilder()
                .email("common@test.com").login("common").build());

        userStorage.addFriend(user1.getId(), common.getId());
        userStorage.addFriend(user2.getId(), common.getId());

        Collection<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends).extracting(User::getId)
                .containsExactly(common.getId());
    }

    @Test
    void testGetFriendsEmpty() {
        User user = userStorage.create(createTestUser());

        Collection<User> friends = userStorage.getFriends(user.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    void testGetCommonFriendsNone() {
        User user1 = userStorage.create(createTestUser().toBuilder()
                .email("user1@test.com").login("user1").build());
        User user2 = userStorage.create(createTestUser().toBuilder()
                .email("user2@test.com").login("user2").build());

        Collection<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends).isEmpty();
    }
}
