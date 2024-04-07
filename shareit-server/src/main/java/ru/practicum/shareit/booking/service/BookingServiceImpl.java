package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptoins.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDtoOut createBooking(Long userId, BookingDtoIn bookingDtoIn) {
        User user = findUserById(userId);
        Item item = findItemById(bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new BookingValidationException("Вещь не достпуна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя забронировать собственную вещь");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoIn, user, item);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    @Transactional
    public BookingDtoOut approveBooking(Long bookingId, Long ownerId, Boolean isApproved) {
        Booking booking = findBookingById(bookingId);
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingValidationException("Бронирование было подтверждено ранее");
        }
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не является владельцем вещи " +
                    " с бронированием id=%d", ownerId, bookingId));
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        log.info("Пользователь с id={} подтвердил бронирование вещи с id={}", ownerId, bookingId);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public BookingDtoOut getBooking(Long bookingId, Long userId) {
        User user = findUserById(userId);
        Booking booking = findBookingById(bookingId);
        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new AccessException("Просмотр информации о бронировании доступен только владельцу вещи или " +
                    "владельцу брони");
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public Collection<BookingDtoOut> getAllBookingsByUser(Long userId, String state, Integer from, Integer size) {
        StateOfBookingRequest stateIn = getState(state);
        User user = findUserById(userId);
        PageRequest page = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> userBookings = bookingRepository.findByBooker(user, page);
        log.info("Список всех бронирований со статусом {} пользователя с id={} успешно получен", state, userId);
        return getBookingsByState(userBookings, stateIn)
                .stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDtoOut> getBookingsForUserItems(Long userId, String state, Integer from, Integer size) {
        StateOfBookingRequest stateIn = getState(state);
        User user = findUserById(userId);
        PageRequest page = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> userBookings = bookingRepository.findByItemOwner(user, page);
        log.info("Список бронирований со статусом {} для вещей пользователя с id={} успешно получен", state, userId);
        return getBookingsByState(userBookings, stateIn)
                .stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id=%d не найдено", bookingId)));
    }

    private Collection<Booking> getBookingsByState(List<Booking> allBookings, StateOfBookingRequest state) {
        Stream<Booking> bookingStream = allBookings.stream();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                bookingStream = bookingStream.filter(booking -> booking.getStart().isBefore(now) &&
                        booking.getEnd().isAfter(now));
                break;
            case PAST:
                bookingStream = bookingStream.filter(booking -> booking.getEnd().isBefore(now));
                break;
            case FUTURE:
                bookingStream = bookingStream.filter(booking -> booking.getStart().isAfter(now));
                break;
            case WAITING:
                bookingStream = bookingStream.filter(booking -> booking.getStatus().equals(BookingStatus.WAITING));
                break;
            case REJECTED:
                bookingStream = bookingStream.filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED));
                break;
        }
        return bookingStream.sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
    }

    private StateOfBookingRequest getState(String state) {
        try {
            return StateOfBookingRequest.valueOf(state);
        } catch (Throwable e) {
            throw new RequestException("Unknown state: " + state);
        }
    }
}
