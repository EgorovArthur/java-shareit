package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptoins.AccessException;
import ru.practicum.shareit.exceptoins.CommentRequestException;
import ru.practicum.shareit.exceptoins.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ItemServiceImplTest {

    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentMapper commentMapper;
    @InjectMocks
    ItemServiceImpl itemService;

    private final User user = new User(1L, "UserName", "username@mail.ru");
    private final User booker = new User(2L, "bookerName", "booker@mail.ru");
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .requestId(1L)
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(itemDto.getRequestId())
            .description("request desc")
            .build();
    private final Item item = Item.builder().id(1L).name("item2Name").description("item2Desc").available(true)
            .owner(user).build();
    private final Comment comment = Comment.builder().id(1L).text("comment1").item(item).author(user)
            .created(LocalDateTime.now()).build();
    private final CommentShortDto commentShortDto = CommentShortDto.builder().id(1L).text("comment1")
            .itemId(item.getId()).authorName(user.getName()).created(LocalDateTime.now()).build();
    private final CommentDto commentDto = CommentDto.builder().id(1L).text("comment1").item(ItemMapper.toItemDto(item))
            .authorName(user.getName()).created(comment.getCreated()).build();

    @Test
    void updateItem() {
        Item item = ItemMapper.toItem(itemDto, user);
        item.setRequest(itemRequest);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("UpdateName")
                .description("Update desc")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto result = itemService.updateItem(user.getId(), item.getId(), itemUpdateDto);

        assertEquals("UpdateName", result.getName());
        assertEquals("Update desc", result.getDescription());

        verify(itemRepository).findById(item.getId());
        verify(userRepository).findById(user.getId());
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_whenAvailableChanged_shouldSetAvailable() {
        Item item = ItemMapper.toItem(itemDto, user);
        item.setAvailable(false);
        item.setRequest(itemRequest);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .available(true)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto result = itemService.updateItem(user.getId(), item.getId(), itemUpdateDto);

        assertTrue(result.getAvailable());

        verify(itemRepository).findById(item.getId());
        verify(userRepository).findById(user.getId());
        verify(itemRepository).save(item);
    }


    @Test
    void updateItem_shouldThrowNotFoundException_whenUserNotFound() {
        Item item = ItemMapper.toItem(itemDto, user);
        item.setRequest(itemRequest);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("UpdateName")
                .description("Update desc")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), item.getId(), itemUpdateDto));
    }

    @Test
    void updateItem_shouldThrowAccessException_whenUserIsNotOwner() {
        Item item = ItemMapper.toItem(itemDto, user);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("UpdateName")
                .description("Update desc")
                .build();

        User notOwner = new User(2L, "NotOwnerName", "notowner@mail.ru");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        item.setOwner(notOwner);

        assertThrows(AccessException.class, () -> itemService.updateItem(user.getId(), item.getId(), itemUpdateDto));
    }

    @Test
    void addItem() {
        Item item = ItemMapper.toItem(itemDto, user);
        item.setRequest(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto createdItem = itemService.addItem(user.getId(), itemDto);

        assertNotNull(createdItem);
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertEquals(itemDto.getRequestId(), createdItem.getRequestId());

        verify(userRepository).findById(user.getId());
        verify(requestRepository).findById(itemDto.getRequestId());
        verify(itemRepository).save(item);
    }

    @Test
    void addItem_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));

        verify(userRepository).findById(user.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addItem_whenRequestNotFound_shouldThrowNotFoundException() {
        ItemDto itemDtoWithNotRequestId = ItemDto.builder().id(1L).name("itemName").description("itemDesc")
                .available(true).requestId(100L).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(requestRepository.findById(itemDtoWithNotRequestId.getRequestId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemDtoWithNotRequestId));

        verify(userRepository).findById(user.getId());
        verify(requestRepository).findById(100L);
        verify(itemRepository, never()).save(any(Item.class));
    }


    @Test
    void getItemById() {
        Item item = Item.builder().id(1L).name("item2Name").description("item2Desc").available(true)
                .owner(user).build();
        List<Comment> comments = new ArrayList<>();

        Booking bookingOne = Booking.builder().id(1L).start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .item(item).booker(booker).status(BookingStatus.APPROVED).build();
        Booking bookingTwo = Booking.builder().id(2L).start(LocalDateTime.now().plusHours(3))
                .end(LocalDateTime.now().plusHours(5))
                .item(item).booker(booker).status(BookingStatus.WAITING).build();

        List<Booking> bookings = List.of(bookingOne, bookingTwo);

        ItemDto expectedItemDto = ItemMapper.toItemDto(item, comments, bookings);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemOrderByIdAsc(item)).thenReturn(comments);
        when(bookingRepository.findByItem(item)).thenReturn(bookings);

        ItemDto actualItemDto = itemService.getItemById(item.getId(), user.getId());

        assertEquals(expectedItemDto, actualItemDto);

        verify(itemRepository).findById(item.getId());
        verify(commentRepository).findByItemOrderByIdAsc(item);
        verify(bookingRepository).findByItem(item);
        verifyNoMoreInteractions(itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void testGetItemByIdWithWrongItem() {
        when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        Exception e = new NotFoundException("Объект не найден");

        assertThrows(e.getClass(), () -> itemService.getItemById(100L, 1L));
        assertEquals("Объект не найден", e.getMessage());

        verify(itemRepository).findById(100L);
    }

    @Test
    void getAllItemsByOwnerId() {
        Long userId = user.getId();
        Item item = ItemMapper.toItem(itemDto, user);
        Item secondItem = Item.builder().id(1L).name("item2Name").description("item2Desc").available(true)
                .owner(user).build();

        List<Item> items = Arrays.asList(item, secondItem);
        int from = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwner(user, PageRequest.of(from, size))).thenReturn(items);
        when(commentRepository.findByItemOrderByIdAsc(item)).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemOrderByIdAsc(secondItem)).thenReturn(Collections.emptyList());
        when(bookingRepository.findByItem(item)).thenReturn(Collections.emptyList());
        when(bookingRepository.findByItem(secondItem)).thenReturn(Collections.emptyList());

        Collection<ItemDto> userItems = itemService.getAllItemsByOwnerId(userId, from, size);

        assertNotNull(userItems);
        assertEquals(2, userItems.size());

        verify(userRepository).findById(userId);
        verify(itemRepository).findByOwner(user, PageRequest.of(0, size));
        verify(commentRepository, times(2)).findByItemOrderByIdAsc(any(Item.class));
        verify(bookingRepository, times(2)).findByItem(any(Item.class));
    }

    @Test
    void findAll() {
        when(itemRepository.findAll()).thenReturn(List.of(ItemMapper.toItem(itemDto, user)));

        Collection<ItemDto> itemDtos = itemService.findAll();

        assertFalse(itemDtos.isEmpty());
        assertEquals(1, itemDtos.size());
    }

    @Test
    void testFindAllItemsWithEmptyList() {
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        Collection<ItemDto> allDtoItems = itemService.findAll();

        assertTrue(allDtoItems.isEmpty());
    }

    @Test
    void searchItems() {
        ItemDto itemDtoForSearch = ItemDto.builder().id(2L).name("Робот-пылесос").description("Моет, чистит")
                .available(true).build();
        Item itemForSearch = ItemMapper.toItem(itemDtoForSearch, user);

        when(itemRepository.search("робот", PageRequest.of(0, 10, Sort.by("name")
                .ascending())))
                .thenReturn(List.of(itemForSearch));

        Collection<ItemDto> actualItems = itemService.searchItems(1L, "робот", 0, 10);

        assertEquals(1, actualItems.size());
        assertTrue(actualItems.contains(itemDtoForSearch));

        verify(itemRepository).search("робот", PageRequest.of(0, 10, Sort.by("name")
                .ascending()));
    }

    @Test
    void addNewComment() {
        when(commentMapper.toComment(any(), any(), any())).thenReturn(comment);
        when(commentRepository.save(any())).thenReturn(comment);
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStart(any(), any(), any()))
                .thenReturn(true);
        CommentDto addedComment = itemService.addNewComment(commentShortDto, item.getId(), user.getId());

        assertEquals(comment.getText(), addedComment.getText());
        assertEquals(comment.getId(), addedComment.getId());
        assertEquals(comment.getAuthor().getName(), addedComment.getAuthorName());
        assertEquals(comment.getItem().getId(), addedComment.getItem().getId());

        verify(bookingRepository).existsBookingByItemAndBookerAndStatusNotAndStart(eq(item), eq(user), any());
        verify(commentRepository).save(any());
    }

    @Test
    public void testAddNewCommentWithNotBooking() {
        when(commentMapper.toComment(any(), any(), any())).thenReturn(comment);
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStart(any(), any(), any()))
                .thenReturn(false);

        assertThrows(CommentRequestException.class, () -> itemService
                .addNewComment(commentShortDto, item.getId(), user.getId()));

        verify(bookingRepository).existsBookingByItemAndBookerAndStatusNotAndStart(eq(item), eq(user), any());
        verify(commentRepository, never()).save(comment);
    }

    @Test
    public void testAddNewCommentWithNotFoundItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService
                .addNewComment(commentShortDto, item.getId(), user.getId()));

        verify(itemRepository).findById(eq(item.getId()));
        verify(bookingRepository, never()).existsBookingByItemAndBookerAndStatusNotAndStart(any(), any(), any());
        verify(commentRepository, never()).save(comment);
    }

    @Test
    public void testAddNewCommentWithNotFoundUser() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService
                .addNewComment(commentShortDto, item.getId(), user.getId()));

        verify(itemRepository).findById(eq(item.getId()));
        verify(userRepository).findById(eq(user.getId()));
        verify(bookingRepository, never()).existsBookingByItemAndBookerAndStatusNotAndStart(any(), any(), any());
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void getCommentById() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        CommentDto result = itemService.getCommentById(comment.getId());

        assertEquals(result, commentDto);
    }

    @Test
    public void testGetCommentWithNotId() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getCommentById(comment.getId()));
    }
}