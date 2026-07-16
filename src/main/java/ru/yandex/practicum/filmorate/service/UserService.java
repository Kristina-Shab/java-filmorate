package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.UserStorage;
import ru.yandex.practicum.filmorate.dto.request.UserCreatRequestDto;
import ru.yandex.practicum.filmorate.dto.request.UserUpdateRequestDto;
import ru.yandex.practicum.filmorate.dto.response.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("db") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<UserResponseDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserResponseDto)
                .toList();
    }

    public UserResponseDto create(UserCreatRequestDto request) {
        User user = UserMapper.mapToUser(request);
        User savedUser = userStorage.create(user);
        return UserMapper.mapToUserResponseDto(savedUser);
    }

    public UserResponseDto update(UserUpdateRequestDto request) {
        if (request.getId() == null) {
            log.warn("Не указан id для обновления");
            throw new ValidationException("Id должен быть указан");
        }
        User updatedUser = userStorage.getEntity(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + request.getId() + " не найден"));
        User savedUser = userStorage.update(updatedUser);
        return UserMapper.mapToUserResponseDto(savedUser);
    }

    public UserResponseDto addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        log.info("Добавление в друзья: пользователь={}, друг={}", user.getLogin(), friend.getLogin());
        if (userStorage.areFriends(userId, friendId)) {
            log.warn("Попытка добавить существующего друга: пользователь={}, друг={}", user.getLogin(), friend.getLogin());
            throw new ValidationException("Пользователь с id " + friend.getId() + "уже является вашим другом.");
        }

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь {} успешно добавлен в друзья к {}", friend.getLogin(), user.getLogin());
        return UserMapper.mapToUserResponseDto(user);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        log.info("Удаление из друзей: пользователь={}, друг={}", user.getLogin(), friend.getLogin());
        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} успешно удален из друзей {}", friend.getLogin(), user.getLogin());
        log.debug("Детали пользователя после удаления друга: {}", user);
    }

    public Collection<UserResponseDto> getFriends(Long userId) {
        getUserById(userId);

        log.info("Получение всех друзей пользователя {}", userId);
        return userStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserResponseDto)
                .toList();
    }

    public Collection<UserResponseDto> getCommonFriends(Long userId, Long otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);
        log.info("Получение общих друзей пользователей {} и {}", userId, otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId).stream()
                .map(UserMapper::mapToUserResponseDto)
                .toList();
    }

    public Optional<UserResponseDto> findById(Long id) {
        return userStorage.getEntity(id)
                .map(UserMapper::mapToUserResponseDto);
    }

    public User getUserById(Long id) {
        return userStorage.getEntity(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
