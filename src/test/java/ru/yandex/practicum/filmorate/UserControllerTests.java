package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTests {
    private UserController userController;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    void createValidUser() {
        User user = validUser();
        User createdUser = userController.create(user);

        assertNotNull(createdUser.getId());
        assertEquals(1, userController.findAll().size());
    }

    @Test
    void createValidUserNameIsBlank() {
        User user = validUser().toBuilder()
                .name("")
                .build();
        User createdUser = userController.create(user);

        assertNotNull(createdUser.getId());
        assertEquals(1, userController.findAll().size());
        assertEquals(createdUser.getLogin(), createdUser.getName());
    }

    @Test
    void createMultipleValidUsers() {
        User user = validUser();
        User createdUser1 = userController.create(user);
        User createdUser2 = userController.create(user);
        User createdUser3 = userController.create(user);

        assertNotNull(createdUser1.getId());
        assertNotNull(createdUser2.getId());
        assertNotNull(createdUser3.getId());
        assertEquals(3, userController.findAll().size());
    }

    @Test
    void updateValidUser() {
        User user = validUser();
        User createdUser = userController.create(user);
        User newUser = validUser().toBuilder()
                .id(createdUser.getId())
                .login("Lichi")
                .build();
        User updatedUser = userController.update(newUser);

        assertEquals(1, userController.findAll().size());
        assertEquals(newUser.getLogin(), updatedUser.getLogin());
    }

    @Test
    void updateInvalidUserIdDoesNotExist() {
        User user = validUser().toBuilder()
                .id(5L)
                .build();
        assertThrows(NotFoundException.class, () -> userController.update(user));
    }

    @Test
    void updateValidUserNameIsBlank() {
        User createUser = createAndSaveValidUser();
        User newUser = createUser.toBuilder()
                .login("Pink")
                .name("")
                .build();
        User updateUser = userController.update(newUser);

        assertEquals(1, userController.findAll().size());
        assertEquals(updateUser.getLogin(), updateUser.getName());
    }

    @Test
    void updateInvalidUserIsBlank() {
        User newEmptyUser = User.builder().build();

        assertThrows(ValidationException.class, () -> userController.update(newEmptyUser));
    }

    @Test
    void addFriendValidUsers() {
        User user = createAndSaveValidUser();
        User friend = createAndSaveValidUser();

        User userWithFriends = userController.addFriend(user.getId(), friend.getId());
        assertEquals(1, userController.findFriends(userWithFriends.getId()).size());
    }

    @Test
    void addFriendInvalidUsersWhenUserAlreadyFriend() {
        User user = createAndSaveValidUser();
        User friend = createAndSaveValidUser();

        userController.addFriend(user.getId(), friend.getId());
        assertThrows(ValidationException.class, () -> userController.addFriend(user.getId(), friend.getId()));
    }

    @Test
    void addFriendMultipleValidUsers() {
        User user = createAndSaveValidUser();
        User friend1 = createAndSaveValidUser();
        User friend2 = createAndSaveValidUser();
        User friend3 = createAndSaveValidUser();

        userController.addFriend(user.getId(), friend1.getId());
        userController.addFriend(user.getId(), friend2.getId());
        User userWithFriends = userController.addFriend(user.getId(), friend3.getId());

        assertEquals(3, userController.findFriends(userWithFriends.getId()).size());
    }

    @Test
    void deleteFriend() {
        User user = createAndSaveValidUser();
        User friend = createAndSaveValidUser();

        userController.addFriend(user.getId(), friend.getId());
        userController.deleteFriend(user.getId(), friend.getId());

        assertEquals(0, userController.findFriends(user.getId()).size());
    }

    private User validUser() {
        return User.builder()
                .login("Nik")
                .email("niki@mail.ru")
                .name("Никита")
                .birthday(LocalDate.of(2020, 11, 25))
                .build();
    }

    private User createAndSaveValidUser() {
        User user = validUser();
        return userController.create(user);
    }


}
