package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.ValidLogin;

public class LoginValidator implements ConstraintValidator<ValidLogin, String> {
    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login == null || login.isBlank()) return false;
        return !login.contains(" ");
    }
}
