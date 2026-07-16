package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.response.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MpaRatingMapper {
    public static MpaRatingDto mapToMpaRatingDto(MpaRating mpaRating) {
        if (mpaRating == null) {
            return null;
        }
        return MpaRatingDto.builder()
                .id(mpaRating.getId())
                .name(mpaRating.getName())
                .description(mpaRating.getDescription())
                .build();
    }
}
