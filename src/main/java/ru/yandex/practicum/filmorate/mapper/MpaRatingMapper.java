package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MpaRatingMapper {
    public static MpaResponseDto mapToMpaRatingDto(MpaRating mpaRating) {
        if (mpaRating == null) {
            return null;
        }
        return MpaResponseDto.builder()
                .id(mpaRating.getId())
                .name(mpaRating.getName())
                .build();
    }
}
