package ru.practicum.shareit_gatevay.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gatevay.user.dto.UserDto;
import ru.practicum.shareit_gatevay.user.dto.UserUpdateDto;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    //Создание пользователя
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Создан новый пользователь с именем {}", userDto.getName());
        return userClient.addUser(userDto);
    }

    //Получение всех пользователей
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получен список всех пользователей.");
        return userClient.getUsers();
    }

    //Получение пользователя по id
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserId(@PathVariable Long userId) {
        log.info("Получен пользователь с id {}", userId);
        return userClient.getUserById(userId);
    }

    //Обновление данных пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody @Valid UserUpdateDto userDto) {
        log.info("Пользователь с id {} обновлен", userId);
        return userClient.updateUser(userId, userDto);
    }

    //Удаление пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Пользователь с id {} удален", userId);
        return userClient.deleteUser(userId);
    }
}
