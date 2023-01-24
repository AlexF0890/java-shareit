package ru.practicum.shareit.exception;

public class BookingStateNotFoundException extends RuntimeException {
    public BookingStateNotFoundException(String e) {
        super(e);
    }
}
