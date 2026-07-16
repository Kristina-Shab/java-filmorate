package ru.yandex.practicum.filmorate.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.request.MpaDto;

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
    private MpaDto mpa;
    private List<GenreRequestDto> genres;
}
