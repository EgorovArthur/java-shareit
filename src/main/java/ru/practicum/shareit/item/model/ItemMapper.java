package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        List<CommentShortDto> commentsShortDto = comments.stream()
                .map(CommentMapper::toCommentShortDto).collect(Collectors.toList());
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(commentsShortDto.isEmpty() ? Collections.emptyList() : commentsShortDto)
                .build();
    }

    public static ItemDto toItemDto(Item item, List<Comment> itemComments, List<Booking> bookings) {
        ItemDto itemDto = toItemDto(item, itemComments);
        LocalDateTime now = LocalDateTime.now();
        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(now) &&
                    (booking.getStatus() == BookingStatus.WAITING ||
                            booking.getStatus() == BookingStatus.APPROVED)) {
                itemDto.setNextBooking(BookingMapper.toShortBookingDto(booking));
            }
            if (booking.getStart().isBefore(now) &&
                    (booking.getStatus() == BookingStatus.WAITING ||
                            booking.getStatus() == BookingStatus.APPROVED)) {
                itemDto.setLastBooking(BookingMapper.toShortBookingDto(booking));
            }
        }
        return itemDto;
    }
}