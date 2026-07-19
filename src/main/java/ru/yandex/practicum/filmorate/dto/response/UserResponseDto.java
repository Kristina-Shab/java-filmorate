package ru.yandex.practicum.filmorate.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class UserResponseDto {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
