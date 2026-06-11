package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTests {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
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
        assertThrows(ValidationException.class, () -> userController.update(user));
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
