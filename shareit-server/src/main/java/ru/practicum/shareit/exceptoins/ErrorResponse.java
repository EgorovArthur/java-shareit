package ru.practicum.shareit.exceptoins;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorResponse {
    private final String error;

    public String getError() {
        return error;
    }
}
