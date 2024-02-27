package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptoins.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto addUser(UserDto userDto) throws ValidationException;

    UserDto getUserId(Long userId);

    UserDto updateUser(Long userId, UserDto userDto) throws ValidationException;

    void deleteUser(Long userId);

    Collection<UserDto> getUsers();

}
