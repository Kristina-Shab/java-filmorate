package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ValidLogin;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    Long id;

    @ValidLogin
    String login;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    String email;
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}
