package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.request.FilmCreateRequestDto;
import ru.yandex.practicum.filmorate.dto.request.FilmUpdateRequestDto;
import ru.yandex.practicum.filmorate.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.ArrayList;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static Film mapToFilm(FilmCreateRequestDto dto) {
        return Film.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpa(Optional.ofNullable(dto.getMpa())
                        .map(mpaDto -> MpaRating.builder()
                                .id(mpaDto.getId())
                                .build())
                        .orElse(null))
                .genres(Optional.ofNullable(dto.getGenres())
                        .map(genreDtos -> genreDtos.stream()
                                .map(genreDto -> Genre.builder()
                                        .id(genreDto.getId())
                                        .build())
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
                .mpa(film.getMpa() != null ? MpaResponseDto.builder()
                        .id(film.getMpa().getId())
                        .name(film.getMpa().getName())
                        .build() : null)
                .genres(new ArrayList<>())
                .build();

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            dto.setGenres(film.getGenres().stream()
                    .map(genre -> GenreResponseDto.builder()
                            .id(genre.getId())
                            .name(genre.getName())
                            .build())
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
                .mpa(Optional.ofNullable(dto.getMpa())
                        .map(mpaDto -> MpaRating.builder()
                                .id(mpaDto.getId())
                                .build())
                        .orElse(null))
                .genres(Optional.ofNullable(dto.getGenres())
                        .map(genresDto -> genresDto.stream()
                                .map(genreDto -> Genre.builder()
                                        .id(genreDto.getId())
                                        .build())
                                .toList())
                        .orElse(existingFilm.getGenres()))
                .build();
    }
}
