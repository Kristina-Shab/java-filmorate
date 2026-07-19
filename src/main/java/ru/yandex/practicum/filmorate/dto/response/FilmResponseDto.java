package ru.yandex.practicum.filmorate.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class FilmResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private MpaResponseDto mpa;
    private List<GenreResponseDto> genres;
}
