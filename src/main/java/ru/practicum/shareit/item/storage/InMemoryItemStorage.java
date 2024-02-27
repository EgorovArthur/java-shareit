package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptoins.AccessException;
import ru.practicum.shareit.exceptoins.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long generatorId = 0L;
    private final UserStorage userStorage;

    @Override
    public Item addItem(Long userId, Item item) {
        item.setOwner(userStorage.getUserId(userId));
        item.setId(++generatorId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        User user = userStorage.getUserId(userId);
        Item updatedItem = getItemById(itemId);

        if (!updatedItem.getOwner().equals(user)) {
            throw new AccessException("Только владелец может обновить вещь!");
        }

        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && !item.getAvailable().equals(updatedItem.getAvailable())) {
            updatedItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, updatedItem);
        return updatedItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new NotFoundException(String.format("Вещь с id = %d не найдена", itemId));
        }
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }
}
