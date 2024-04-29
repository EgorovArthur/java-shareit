package ru.practicum.shareit_gatevay.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private Long id; // уникальный идентификатор пользователя
    private String name; // имя или логин пользователя
    @Email
    @NotBlank
    @NotNull
    private String email; // адрес электронной почты (2 пользователя не могут иметь одинаковый email)
}
