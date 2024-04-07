package ru.practicum.shareit_gatevay.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gatevay.booking.dto.BookingDtoIn;
import ru.practicum.shareit_gatevay.booking.dto.BookingState;
import ru.practicum.shareit_gatevay.exception.BookingStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;
    private final BookingValidatorTime bookingValidatorTime;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        bookingValidatorTime.validateBookingTime(bookingDtoIn.getStart(), bookingDtoIn.getEnd());
        log.info("Добавление пользователем запроса на бронирование: {} ", userId);
        return bookingClient.addBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        log.info("Подтверждение или отклонение запроса на бронирование: {} ", userId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Получение информации о бронировании: {} ", bookingId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                  @RequestParam(value = "from", required = false,
                                                          defaultValue = "0") @PositiveOrZero final Integer from,
                                                  @RequestParam(value = "size", required = false,
                                                          defaultValue = "10") @Positive final Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStateException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                          @RequestParam(value = "from", required = false,
                                                                  defaultValue = "0") final @PositiveOrZero Integer from,
                                                          @RequestParam(value = "size", required = false,
                                                                  defaultValue = "10") @Positive final Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStateException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsForUserItems(userId, state, from, size);
    }
}
