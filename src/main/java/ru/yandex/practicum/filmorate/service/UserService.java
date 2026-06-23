package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(User user, User friend) {
        if (user.getFriends().contains(friend.getId())) {
            throw new ValidationException("Пользователь с id " + friend.getId() + "уже является вашим другом.");
        }

        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
    }

    public void removeFriend(User user, User friend) {
        if (!user.getFriends().contains(friend.getId())) {
            throw new ValidationException("Невозможно удалить пользователя с id " + friend.getId() + ". Его нет в списке друзей.");
        }

        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public Collection<User> getFriends(User user) {
        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }

    public Collection<User> getCommonFriends(User user, User otherUser) {
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }
}
