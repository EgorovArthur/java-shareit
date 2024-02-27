package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptoins.NotFoundException;
import ru.practicum.shareit.exceptoins.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long generatorId = 0L;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        validateEmail(user.getEmail());
        user.setId(++generatorId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) throws ValidationException {
        User updatedUser = getUserId(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(updatedUser.getEmail())) {
                validateEmail(user.getEmail());
            }
            updatedUser.setEmail(user.getEmail());
        }
        users.put(userId, updatedUser);
        return updatedUser;
    }

    @Override
    public User getUserId(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
        } else {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    private void validateEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                throw new ValidationException("Данный email " + email + " уже занят");
            }
        }
    }
}
