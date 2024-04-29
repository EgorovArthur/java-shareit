package ru.practicum.shareit_gatevay.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorResponse {

    private final String error;

    public String getError() {
        return error;
    }
}
