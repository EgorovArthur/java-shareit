package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptoins.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        log.info("Пользователь {} добавлен", userDto);
        return UserMapper.toUserDto(userStorage.addUser(user));
    }

    @Override
    public UserDto getUserId(Long userId) {
        log.info("Получаем пользователя по ID: {}", userId);
        return UserMapper.toUserDto(userStorage.getUserId(userId));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws ValidationException {
        User user = UserMapper.toUser(userDto);
        log.info("Обновляем данные пользователя по ID: {}", userId);
        return UserMapper.toUserDto(userStorage.updateUser(userId, user));
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Удаляем пользователя по ID: {}", userId);
        userStorage.deleteUser(userId);
    }

    @Override
    public Collection<UserDto> getUsers() {
        log.info("Получаем всех пользователей");
        return userStorage.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
