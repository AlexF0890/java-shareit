package ru.practicum.shareit.exception;

public class BookingStartTimeAfterEndTimeException extends RuntimeException {
    public BookingStartTimeAfterEndTimeException(String e) {
        super(e);
    }
}