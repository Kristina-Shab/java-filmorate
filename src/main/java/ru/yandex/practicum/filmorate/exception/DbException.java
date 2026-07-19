package ru.yandex.practicum.filmorate.exception;

public class DbException extends RuntimeException {
    public DbException(String message) {
        super(message);
    }
}
