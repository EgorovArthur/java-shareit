package ru.practicum.shareit.item.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Data
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!updatedItem.getOwner().equals(owner)) {
            throw new AccessException("Только владелец может обновить вещь!");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(updatedItem.getAvailable())) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        Item item = itemOptional.orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%d не найдена",
                itemId)));
        List<Comment> comments = commentRepository.findByItemOrderByIdAsc(item);
        List<Booking> bookings = bookingRepository.findByItem(item);
        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDto(item, comments, bookings);
        }
        return ItemMapper.toItemDto(item, comments);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Item> userItems = itemRepository.findByOwner(owner);
        return userItems.stream().map(item ->
                        ItemMapper.toItemDto(item, commentRepository.findByItemOrderByIdAsc(item),
                                bookingRepository.findByItem(item)))
                .sorted(this::compareBookingDates).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findAll() {
        return itemRepository.findAll()
                .stream()
                .map(item -> ItemMapper.toItemDto(item, commentRepository.findByItemOrderByIdAsc(item),
                        bookingRepository.findByItem(item)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        return itemRepository.search(text).stream()
                .filter(item -> item.getAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addNewComment(CommentShortDto commentDto, Long itemId, Long userId) {
        Comment comment = commentMapper.toComment(commentDto);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        comment.setItem(item);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        comment.setAuthor(user);
        if (!bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStart(comment.getItem(),
                comment.getAuthor(), LocalDateTime.now())) {
            throw new CommentRequestException("Нельзя оставить комменатрий к вещи, если она не была взята в аренду" +
                    "или аренда еще не началась");
        }
        comment.setCreated(LocalDateTime.now());
        log.info("Пользователь id={} добавил комментарий id={} к вещи id={}",
                comment.getAuthor().getId(), comment.getId(), comment.getItem().getId());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        return CommentMapper.toCommentDto(commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Комментарий с id=%d не найден", commentId))));
    }

    private int compareBookingDates(ItemDto itemDto1, ItemDto itemDto2) {
        if (itemDto1.getNextBooking() == null && itemDto2.getNextBooking() == null) return 0;
        if (itemDto1.getNextBooking() == null) return 1;
        if (itemDto2.getNextBooking() == null) return -1;
        return -itemDto1.getNextBooking().getStart().compareTo(itemDto2.getNextBooking().getStart());
    }
}
