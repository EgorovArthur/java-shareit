package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptoins.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class BookingServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    private final User user = new User(1L, "Nameuser", "user@mail.ru");
    private final Item item = Item.builder().id(1L).name("item2Name").description("item2Desc").available(true)
            .owner(user).build();
    private final User booker = new User(1L, "bookerName", "booker@mail.ru");
    private final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .itemId(item.getId())
            .bookerId(booker.getId())
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusDays(1))
            .build();

    @Test
    void createBooking() {
        Booking booking = BookingMapper.toBooking(bookingDtoIn, user, item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingDtoOut result = bookingService.createBooking(anyLong(), bookingDtoIn);

        assertNotNull(result);
        assertEquals(bookingDtoIn.getStart(), result.getStart());
        assertEquals(bookingDtoIn.getEnd(), result.getEnd());
        assertEquals(bookingDtoIn.getBookerId(), result.getBooker().getId());

        verify(bookingRepository).save(booking);
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void approveBooking() {
        Booking booking = BookingMapper.toBooking(bookingDtoIn, user, item);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDtoOut result = bookingService.approveBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approveBooking_invalidStatus() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void getBooking() {
        Booking booking = BookingMapper.toBooking(bookingDtoIn, user, item);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BookingDtoOut bookingDtoOut = bookingService.getBooking(1L, 1L);

        assertEquals(1L, bookingDtoOut.getItem().getId());
        verify(bookingRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getAllBookingsByUser() {
    }

    @Test
    void getBookingsForUserItems() {
    }
}