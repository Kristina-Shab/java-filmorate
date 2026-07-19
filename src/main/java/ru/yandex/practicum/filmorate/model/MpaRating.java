package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MpaRating {

    private Long id;
    private String name;
    private String description;

}
