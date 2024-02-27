package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

    private String id; //уникальный идентификатор бронирования;
    private LocalDateTime start; //дата и время начала бронирования;
    private LocalDateTime end; //дата и время конца бронирования;
    private Item item; // вещь, которую пользователь бронирует;
    private User booker; //пользователь, который осуществляет бронирование;
    private BookingStatus status; //статус бронирования;
}
