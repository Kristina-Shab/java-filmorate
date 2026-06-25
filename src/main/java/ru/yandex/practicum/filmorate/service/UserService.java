package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        log.info("Добавление в друзья: пользователь={}, друг={}", user.getLogin(), friend.getLogin());
        if (user.getFriends().contains(friend.getId())) {
            log.warn("Попытка добавить существующего друга: пользователь={}, друг={}", user.getLogin(), friend.getLogin());
            throw new ValidationException("Пользователь с id " + friend.getId() + "уже является вашим другом.");
        }

        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        log.info("Пользователь {} успешно добавлен в друзья к {}", friend.getLogin(), user.getLogin());
        log.debug("Детали пользователя с новым другом: {}", user);
        return user;
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        log.info("Удаление из друзей: пользователь={}, друг={}", user.getLogin(), friend.getLogin());
        user.getFriends().remove(friendId);
        friend.getFriends().remove(user.getId());
        log.info("Пользователь {} успешно удален из друзей {}", friend.getLogin(), user.getLogin());
        log.debug("Детали пользователя после удаления друга: {}", user);
    }

    public Collection<User> getFriends(Long userId) {
        log.info("Получение всех друзей пользователя {}", userId);
        return getUserById(userId).getFriends().stream()
                .map(userStorage::getUser)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        log.info("Получение общих друзей пользователей {} и {}", user.getLogin(), otherUser.getLogin());
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUser)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Optional<User> findById(Long id) {
        return userStorage.getUser(id);
    }

    public User getUserById(Long id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
