package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItemsByOwnerId(Long userId);

    List<ItemDto> searchItems(Long userId, String text);
}
