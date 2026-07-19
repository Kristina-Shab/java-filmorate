package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.request.UserCreateRequestDto;
import ru.yandex.practicum.filmorate.dto.request.UserUpdateRequestDto;
import ru.yandex.practicum.filmorate.dto.response.UserResponseDto;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(UserCreateRequestDto dto) {
        return User.builder()
                .login(dto.getLogin())
                .email(dto.getEmail())
                .name(dto.getName() != null && !dto.getName().isBlank()
                        ? dto.getName()
                        : dto.getLogin())
                .birthday(dto.getBirthday())
                .build();
    }

    public static UserResponseDto mapToUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

    public static User updateUserFields(User existingUser, UserUpdateRequestDto dto) {
        return existingUser.toBuilder()
                .login(dto.getLogin())
                .email(dto.getEmail())
                .name(dto.getName() != null && !dto.getName().isBlank()
                        ? dto.getName()
                        : dto.getLogin())
                .birthday(dto.getBirthday())
                .build();
    }
}
