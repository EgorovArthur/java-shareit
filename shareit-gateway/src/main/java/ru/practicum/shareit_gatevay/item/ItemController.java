package ru.practicum.shareit_gatevay.item;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gatevay.item.dto.CommentShortDto;
import ru.practicum.shareit_gatevay.item.dto.ItemDto;
import ru.practicum.shareit_gatevay.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;


@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    // Добавление новой вещи пользователем
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    // Редактирование вещи
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestBody ItemUpdateDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания
    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(value = "from", required = false, defaultValue = "0")
                                                       @PositiveOrZero final Integer from,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10")
                                                       @Positive final Integer size) {

        log.info("Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой: {}", userId);
        return itemClient.getAllItemsByOwnerId(userId, from, size);
    }

    // Поиск вещи потенциальным арендатором
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam("text") String text,
                                              @RequestParam(value = "from", required = false, defaultValue = "0")
                                              @PositiveOrZero final Integer from,
                                              @RequestParam(value = "size", required = false, defaultValue = "10")
                                              @Positive final Integer size) {

        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        log.info("Пользователь с id={} выполнил поиск вещи {}", userId, text);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createItemComment(@RequestBody final CommentShortDto commentShortDto,
                                                    @PathVariable final Long itemId,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (commentShortDto.getText().isBlank()) {
            throw new IllegalArgumentException("Текст комментария не может быть пустым");
        }
        return itemClient.addNewComment(commentShortDto, itemId, userId);
    }
}
