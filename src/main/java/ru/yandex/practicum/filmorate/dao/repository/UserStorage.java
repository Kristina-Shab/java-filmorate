package ru.yandex.practicum.filmorate.dao.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends BaseStorage<User> {
    boolean areFriends(Long userId, Long friendId);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long id);

    Collection<User> getCommonFriends(Long userId, Long otherUserId);
}
