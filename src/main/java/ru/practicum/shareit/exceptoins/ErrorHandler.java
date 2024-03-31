package ru.practicum.shareit.exceptoins;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("Объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAccessException(final AccessException e) {
        return Map.of("Нет прав доступа", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookingValidationException(final BookingValidationException e) {
        return Map.of("Ошибка запроса на бронирование", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRequestException(final RequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCommentRequestException(final CommentRequestException e) {
        return Map.of("Ошибка запроса ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(final BadRequestException e) {
        return Map.of("Ошибка запроса ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return Map.of("Ошибка валидации ", e.getMessage());
    }
}
