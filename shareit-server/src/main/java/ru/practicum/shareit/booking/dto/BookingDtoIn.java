package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingDtoIn {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private String status;
}
