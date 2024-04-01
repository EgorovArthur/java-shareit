package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntItemServiceImplTest {

    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemServiceImpl itemService;
    @Autowired
    private final BookingRepository bookingRepository;
    private final User user = User.builder().name("user").email("user@mail.ru").build();
    private final Item item = Item.builder().name("itemName").description("item1Desc").available(true)
            .owner(user).build();
    private final Item secondItem = Item.builder().name("item2Name").description("item2Desc").available(true)
            .owner(user).build();
    private final User booker = User.builder().name("user2").email("user2@mail.ru").build();
    private final Booking booking = Booking.builder().start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(2)).item(item).booker(booker)
            .status(BookingStatus.APPROVED).build();

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        userRepository.save(booker);
        itemRepository.save(item);
        itemRepository.save(secondItem);
        bookingRepository.save(booking);
    }

    @Test
    void testGetUserItems() {
        Collection<ItemDto> userItems = itemService.getAllItemsByOwnerId(user.getId(), 0, 10);

        assertNotNull(userItems);
        assertEquals(2, userItems.size());

        ItemDto firstItem = userItems.stream().findFirst().orElse(null);
        assertNotNull(firstItem);
        assertNotNull(firstItem.getId());
        assertNotNull(firstItem.getName());
        assertNotNull(firstItem.getDescription());
        assertNotNull(firstItem.getAvailable());

        ItemDto secondItem = userItems.stream().skip(1).findFirst().orElse(null);
        assertNotNull(secondItem);
        assertNotNull(secondItem.getId());
        assertNotNull(secondItem.getName());
        assertNotNull(secondItem.getDescription());
        assertNotNull(secondItem.getAvailable());
    }

    @Test
    @Transactional
    public void testAddComment() {
        CommentShortDto commentDto = CommentShortDto.builder().text("test comment").build();
        Long itemId = item.getId();
        Long userId = booker.getId();

        CommentDto comment = itemService.addNewComment(commentDto, itemId, userId);

        assertNotNull(comment);
        assertNotNull(comment.getId());
        assertNotNull(comment.getText());
        assertNotNull(comment.getAuthorName());
        assertNotNull(comment.getItem());
        assertNotNull(comment.getCreated());

        assertEquals("test comment", comment.getText());
        assertEquals(itemId, comment.getItem().getId());
        assertEquals(booker.getName(), comment.getAuthorName());
    }
}
