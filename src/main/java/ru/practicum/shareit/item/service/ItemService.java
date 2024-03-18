package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemDto);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllItemsByOwnerId(Long userId);

    List<ItemDto> searchItems(Long userId, String text);

    Collection<ItemDto> findAll();
}
