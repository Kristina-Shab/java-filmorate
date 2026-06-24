package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friend.getId())) {
            throw new ValidationException("Пользователь с id " + friend.getId() + "уже является вашим другом.");
        }

        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        return user;
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            throw new ValidationException("Невозможно удалить пользователя с id " + friendId + ". Его нет в списке друзей.");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(user.getId());
    }

    public Collection<User> getFriends(Long userId) {
        return getUserById(userId).getFriends().stream()
                .map(userStorage::getUser)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUser)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Optional<User> findById (Long id){
        return userStorage.getUser(id);
    }

    public User getUserById(Long id){
        return userStorage.getUser(id)
                .orElseThrow(() -> new ValidationException("Пользователь с id " + id + " не найден"));
    }
}
