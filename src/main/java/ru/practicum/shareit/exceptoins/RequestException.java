package ru.practicum.shareit.exceptoins;

public class RequestException extends RuntimeException {
    public RequestException(String message) {
        super(message);
    }
}

