package ru.practicum.shareit_gatevay.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
@AllArgsConstructor
public class UserUpdateDto {

    private String name;
    @Email
    private String email;
}
