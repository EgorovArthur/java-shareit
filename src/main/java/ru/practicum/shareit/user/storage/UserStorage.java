package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exceptoins.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    User addUser(User user) throws ValidationException;

    User updateUser(Long userId, User user) throws ValidationException;

    User getUserId(Long id);

    void deleteUser(Long userId);
}
