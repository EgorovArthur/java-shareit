package ru.practicum.shareit_gatevay.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gatevay.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createNewRequest(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                   @RequestBody @Valid ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание запроса не может быть пустым!");
        }
        log.info("Создан новый запрос с описанием {}", itemRequestDto.getDescription());
        return itemRequestClient.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") final Long userId) {
        log.info("Получены запросы пользователя c id={}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                 @RequestParam(value = "from", defaultValue = "0")
                                                 @PositiveOrZero final Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10")
                                                 @Positive final Integer size) {
        log.info("Получены все запросы пользователем с id={}", userId);
        return itemRequestClient.getAllRequestsForAllUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                 @PathVariable final Long requestId) {
        log.info("Получен запрос с id={} пользователем с id={}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
