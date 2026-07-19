package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.GenreStorage;
import ru.yandex.practicum.filmorate.dto.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;

import java.util.Collection;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("db") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<GenreResponseDto> findAll() {
        return genreStorage.findAll().stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreResponseDto findById(Long id) {
        return genreStorage.findById(id)
                .map(GenreMapper::mapToGenreDto)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }
}
