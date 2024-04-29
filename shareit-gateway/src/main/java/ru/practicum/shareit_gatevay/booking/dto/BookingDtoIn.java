package ru.practicum.shareit_gatevay.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoIn {

    private Long id;
    @NotNull
    private Long itemId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start; //дата и время начала бронирования;
    @NotNull
    @Future
    private LocalDateTime end; //дата и время конца бронирования;
    private Long bookerId;
    private String status;
}
