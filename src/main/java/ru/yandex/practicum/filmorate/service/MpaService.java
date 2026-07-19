package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repository.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.dto.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;

import java.util.Collection;

@Slf4j
@Service
public class MpaService {
    private final MpaDbStorage mpaStorage;

    public MpaService(@Qualifier("db") MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<MpaResponseDto> findAll() {
        return mpaStorage.findAll().stream()
                .map(MpaRatingMapper::mapToMpaRatingDto)
                .toList();
    }

    public MpaResponseDto findById(Long id) {
        return mpaStorage.findById(id)
                .map(MpaRatingMapper::mapToMpaRatingDto)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
    }
}
