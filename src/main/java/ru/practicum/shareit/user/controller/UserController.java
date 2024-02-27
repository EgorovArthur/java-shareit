package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptoins.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    //Создание пользователя
    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) throws ValidationException {
        return userService.addUser(userDto);
    }

    //Получение всех пользователей
    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getUsers();
    }

    //Получение пользователя по id
    @GetMapping("/{userId}")
    public UserDto getUserId(@PathVariable("userId") Long userId) {
        return userService.getUserId(userId);
    }

    //Обновление данных пользователя
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId, @RequestBody UserDto userDto) throws ValidationException {
        return userService.updateUser(userId, userDto);
    }

    //Удаление пользователя
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }
}