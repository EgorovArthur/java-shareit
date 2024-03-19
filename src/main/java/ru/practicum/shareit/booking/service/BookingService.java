package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.Collection;

public interface BookingService {

    public BookingDtoOut createBooking(Long userId, BookingDtoIn bookingDtoIn);

    public BookingDtoOut approveBooking(Long bookingId, Long owner, Boolean isApproved);

    BookingDtoOut getBooking(Long bookingId, Long userId);

    Collection<BookingDtoOut> getAllBookingsByUser(Long userId, String state);

    Collection<BookingDtoOut> getBookingsForUserItems(Long userId, String state);
}
