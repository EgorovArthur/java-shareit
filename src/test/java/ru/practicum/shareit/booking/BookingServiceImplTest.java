package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptoins.AccessException;
import ru.practicum.shareit.exceptoins.BookingValidationException;
import ru.practicum.shareit.exceptoins.NotFoundException;
import ru.practicum.shareit.exceptoins.RequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final User owner = new User(1L, "user", "user@mail.ru");
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
    void createBooking_shouldThrowBookingValidationException_whenItemIsNotAvailable() {
        Item item1 = Item.builder().id(1L).name("item2Name").description("item2Desc").available(false)
                .owner(user).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(BookingValidationException.class, () -> bookingService.createBooking(anyLong(), bookingDtoIn));

        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void createBooking_shouldThrowNotFoundException_whenUserTriesToBookOwnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(item.getOwner().getId(), bookingDtoIn));

        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verifyNoMoreInteractions(bookingRepository);
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
    void approveBooking_shouldThrowBookingValidationException_whenBookingIsAlreadyApproved() {
        Booking booking = BookingMapper.toBooking(bookingDtoIn, user, item);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BookingValidationException.class, () -> bookingService.approveBooking(1L, 1L, true));

        verify(bookingRepository).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void approveBooking_shouldThrowNotFoundException_whenUserIsNotOwnerOfItem() {
        Booking booking = BookingMapper.toBooking(bookingDtoIn, user, item);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, 2L, true));

        verify(bookingRepository).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
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
    void getBooking_shouldThrowAccessException_whenUserIsNotOwnerOfItemOrBooking() {
        User anotherUser = User.builder()
                .id(2L)
                .name("anotherUser")
                .email("anotherUser@example.com")
                .build();
        Booking booking = BookingMapper.toBooking(bookingDtoIn, user, item);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anotherUser));

        assertThrows(AccessException.class, () -> bookingService.getBooking(1L, 1L));

        verify(bookingRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getAllBookingsByUser() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .booker(user)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByBooker(user, PageRequest.of(0, 10, Sort.by("start").descending())))
                .thenReturn(List.of(booking2, booking1));

        Collection<BookingDtoOut> bookingDtoOuts = bookingService
                .getAllBookingsByUser(1L, "WAITING", 0, 10);

        assertEquals(2, bookingDtoOuts.size());
        assertEquals(2L, bookingDtoOuts.stream().findFirst().get().getId());
        assertEquals(1L, bookingDtoOuts.stream().skip(1).findFirst().get().getId());
        verify(userRepository).findById(1L);
        verify(bookingRepository)
                .findByBooker(user, PageRequest.of(0, 10, Sort.by("start").descending()));
    }

    @Test
    void getBookingsForUserItems() {
        User user2 = User.builder()
                .id(2L)
                .name("User")
                .email("User@example.com")
                .build();
        Booking booking1 = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository
                .findByItemOwner(user, PageRequest.of(0, 10, Sort.by("start").descending())))
                .thenReturn(List.of(booking2, booking1));

        Collection<BookingDtoOut> bookingDtoOuts = bookingService
                .getBookingsForUserItems(1L, "WAITING", 0, 10);

        assertEquals(2, bookingDtoOuts.size());
        assertEquals(2L, bookingDtoOuts.stream().findFirst().get().getId());
        assertEquals(1L, bookingDtoOuts.stream().skip(1).findFirst().get().getId());
        verify(userRepository).findById(1L);
        verify(bookingRepository)
                .findByItemOwner(user, PageRequest.of(0, 10, Sort.by("start").descending()));
    }

    @Test
    void testGetBookingsForUserItemsWithIncorrectStatus() {
        List<Booking> userBookings = new ArrayList<>();
        userBookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.REJECTED));

        String state = "INCORRECT";
        Exception exception = assertThrows(RequestException.class, () -> bookingService
                .getBookingsForUserItems(owner.getId(), state, 0, 10));
        assertEquals("Unknown state: " + state, exception.getMessage());
    }

    @Test
    void testGetBookingsForUserItemsWithRejectedStatus() {
        List<Booking> userBookings = new ArrayList<>();
        userBookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.REJECTED));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwner(any(User.class), any(Pageable.class))).thenReturn(userBookings);

        Collection<BookingDtoOut> results = bookingService
                .getBookingsForUserItems(owner.getId(), "REJECTED", 0, 10);

        assertEquals(1, results.size());
    }

    @Test
    void testGetBookingsForUserItemsWithPastStatus() {
        List<Booking> userBookings = new ArrayList<>();
        userBookings.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED));
        userBookings.add(new Booking(2L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), item, booker, BookingStatus.APPROVED));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwner(any(User.class), any(Pageable.class))).thenReturn(userBookings);

        Collection<BookingDtoOut> results = bookingService
                .getBookingsForUserItems(owner.getId(), "PAST", 0, 10);

        assertEquals(2, results.size());
    }

    @Test
    void testGetBookingsForUserItemsWithFutureStatus() {
        List<Booking> userBookings = new ArrayList<>();
        userBookings.add(new Booking(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED));
        userBookings.add(new Booking(2L, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(4), item, booker, BookingStatus.APPROVED));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwner(any(User.class), any(Pageable.class))).thenReturn(userBookings);

        Collection<BookingDtoOut> results = bookingService
                .getBookingsForUserItems(owner.getId(), "FUTURE", 0, 10);

        assertEquals(2, results.size());
    }

    @Test
    void testBookingShortDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        BookingShortDto convertedDto = BookingMapper.toShortBookingDto(booking);

        assertNotNull(convertedDto.getStart());
        assertNotNull(convertedDto.getEnd());
        assertEquals(booking.getItem().getId(), convertedDto.getItemId());
        assertEquals(booking.getBooker().getId(), convertedDto.getBookerId());
        assertEquals(booking.getStatus().toString(), convertedDto.getStatus());
    }
}
