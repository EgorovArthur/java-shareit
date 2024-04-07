package ru.practicum.shareit_gatevay.user;

import lombok.RequiredArgsConstructor;
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
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    //Создание пользователя
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {
        return userClient.addUser(userDto);
    }

    //Получение всех пользователей
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    //Получение пользователя по id
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserId(@PathVariable Long userId) {
        return userClient.getUserById(userId);
    }

    //Обновление данных пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody @Valid UserUpdateDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    //Удаление пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        return userClient.deleteUser(userId);
    }
}
