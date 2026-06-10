package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    Long id;
    String login;
    String email;
    String name;
    LocalDate birthday;
}
