package ru.practicum.shareit.exceptoins;

public class CommentRequestException extends RuntimeException {
    public CommentRequestException(String message) {
        super(message);
    }
}
