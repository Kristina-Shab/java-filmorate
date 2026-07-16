package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.request.FilmCreatRequestDto;
import ru.yandex.practicum.filmorate.dto.request.FilmUpdateRequestDto;
import ru.yandex.practicum.filmorate.dto.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.request.MpaDto;
import ru.yandex.practicum.filmorate.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static Film mapToFilm(FilmCreatRequestDto dto) {
        return Film.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpaRatingId(Optional.ofNullable(dto.getMpa()).map(MpaDto::getId).orElse(null))
                .genre(Optional.ofNullable(dto.getGenres())
                        .map(genres -> genres.stream()
                                .map(GenreRequestDto::getId)
                                .toList())
                        .orElse(null))
                .build();
    }

    public static FilmResponseDto mapToFilmResponseDto(Film film) {
        FilmResponseDto dto = FilmResponseDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(new MpaDto(film.getMpaRatingId()))
                .build();

        if (film.getGenre() != null && !film.getGenre().isEmpty()) {
            dto.setGenres(film.getGenre().stream()
                    .map(GenreRequestDto::new)
                    .toList());
        }

        return dto;
    }

    public static Film updateFilmFields(Film existingFilm, FilmUpdateRequestDto dto) {
        return existingFilm.toBuilder()
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpaRatingId(Optional.ofNullable(dto.getMpa()).map(MpaDto::getId).orElse(null))
                .build();
    }
}
