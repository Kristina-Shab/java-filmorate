package ru.yandex.practicum.filmorate.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class GenreDto {
    private Long id;
    private String name;
}
