package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.persistence.Column;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id; // уникальный идентификатор вещи
    private String name; // краткое название
    private String description; // развернутое описание
    private Boolean available; // статус о доступности вещи для аренды
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentShortDto> comments;
    @Column(name = "request_id")
    private Long requestId;
}

