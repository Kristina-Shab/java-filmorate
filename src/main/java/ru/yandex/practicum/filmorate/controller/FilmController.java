package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.request.FilmCreateRequestDto;
import ru.yandex.practicum.filmorate.dto.request.FilmUpdateRequestDto;
import ru.yandex.practicum.filmorate.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmResponseDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<FilmResponseDto> findById(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public Collection<FilmResponseDto> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }

    @PostMapping
    public FilmResponseDto create(@Valid @RequestBody FilmCreateRequestDto request) {
        return filmService.create(request);
    }

    @PutMapping
    public FilmResponseDto update(@Valid @RequestBody FilmUpdateRequestDto request) {
        return filmService.update(request);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmResponseDto addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }
}
