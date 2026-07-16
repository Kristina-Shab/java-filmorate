package ru.yandex.practicum.filmorate.dao.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends BaseStorage<User> {
    boolean areFriends(Long id1, Long id2);

    void addFriend(Long id1, Long id2);

    void removeFriend(Long id1, Long id2);

    Collection<User> getFriends(Long id);

    Collection<User> getCommonFriends(Long userId, Long otherUserId);
}
