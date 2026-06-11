package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
public class Film {
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    @ValidReleaseDate
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    int duration;
}
