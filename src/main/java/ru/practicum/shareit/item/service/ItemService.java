package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemDto);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllItemsByOwnerId(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(Long userId, String text, Integer from, Integer size);

    Collection<ItemDto> findAll();

    CommentDto addNewComment(CommentShortDto commentShortDto, Long itemId, Long userId);

    CommentDto getCommentById(Long commentId);
}
