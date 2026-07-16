package ru.yandex.practicum.filmorate.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MpaRatingDto {
    private Integer id;
    private String name;
    private String description;
}
