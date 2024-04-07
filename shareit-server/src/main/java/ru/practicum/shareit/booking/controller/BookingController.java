package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptoins.BadRequestException;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Добавление пользователем запроса на бронирование: {} ", userId);
        return bookingService.createBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        log.info("Подтверждение или отклонение запроса на бронирование: {} ", userId);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId) {
        log.info("Получение информации о бронировании: {} ", bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDtoOut> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(value = "from", required = false,
                                                             defaultValue = "0") final Integer from,
                                                     @RequestParam(value = "size", required = false,
                                                             defaultValue = "10") final Integer size) {
        if (from < 0 || size < 0) {
            throw new BadRequestException("Значение from и size не могут быть меньше 0");
        }
        return bookingService.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOut> getBookingsForUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(defaultValue = "ALL") String state,
                                                             @RequestParam(value = "from", required = false,
                                                                     defaultValue = "0") final Integer from,
                                                             @RequestParam(value = "size", required = false,
                                                                     defaultValue = "10") final Integer size) {
        if (from < 0 || size < 0) {
            throw new BadRequestException("Значение from и size не могут быть меньше 0");
        }
        return bookingService.getBookingsForUserItems(userId, state, from, size);
    }
}
