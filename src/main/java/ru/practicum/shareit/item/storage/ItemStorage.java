package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Item getItemById(Long itemId);

    List<Item> findAll();
}
