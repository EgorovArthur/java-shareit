package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;

    private final User owner = User.builder().name("UserName").email("user@mail.ru").build();
    private final User booker = User.builder().name("UserBooker").email("booker@mail.ru").build();
    private final Item item = Item.builder()
            .name("Книга")
            .description("Война и Мир")
            .available(true)
            .owner(owner)
            .build();
    private final Booking booking = Booking.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(5))
            .booker(booker)
            .item(item)
            .status(BookingStatus.WAITING)
            .build();

    @BeforeEach
    void setUp() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findByItem() {
        List<Booking> actualBookings = bookingRepository.findByItem(item);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());

        Booking actualBooking = actualBookings.get(0);
        assertNotNull(actualBooking.getStart());
        assertNotNull(actualBooking.getEnd());
        assertEquals(item, actualBooking.getItem());
        assertEquals(booker, actualBooking.getBooker());
        assertEquals(BookingStatus.WAITING, actualBooking.getStatus());
    }


    @Test
    void findByBooker() {
        List<Booking> actualBookings = bookingRepository.findByBooker(booker, PageRequest.of(0,20));

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());

        Booking actualBooking = actualBookings.get(0);
        assertNotNull(actualBooking.getStart());
        assertNotNull(actualBooking.getEnd());
        assertEquals(item, actualBooking.getItem());
        assertEquals(booker, actualBooking.getBooker());
        assertEquals(BookingStatus.WAITING, actualBooking.getStatus());

    }

    @Test
    void findByItemOwner() {
        List<Booking> actualBookings = bookingRepository.findByItemOwner(owner, PageRequest.of(0,20));

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());

        Booking actualBooking = actualBookings.get(0);
        assertNotNull(actualBooking.getStart());
        assertNotNull(actualBooking.getEnd());
        assertEquals(item, actualBooking.getItem());
        assertEquals(booker, actualBooking.getBooker());
        assertEquals(BookingStatus.WAITING, actualBooking.getStatus());
    }

    @Test
    void existsBookingByItemAndBookerAndStatusNotAndStart() {
        Boolean result = bookingRepository
                .existsBookingByItemAndBookerAndStatusNotAndStart(item, booker, LocalDateTime.now().plusMinutes(40));

        assertTrue(result);
    }
}