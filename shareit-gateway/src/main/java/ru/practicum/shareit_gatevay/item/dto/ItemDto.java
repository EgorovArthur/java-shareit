package ru.practicum.shareit_gatevay.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit_gatevay.booking.dto.BookingShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id; // уникальный идентификатор вещи
    @NotNull
    @NotEmpty
    @NotBlank
    private String name; // краткое название
    @NotNull
    @NotEmpty
    @NotBlank
    private String description; // развернутое описание
    @NotNull
    private Boolean available; // статус о доступности вещи для аренды
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentShortDto> comments;
    private Long requestId;
}
