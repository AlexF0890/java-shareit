package ru.practicum.shareit.exception;

import ru.practicum.shareit.booking.STATE;

public class BookingStateNotFoundException extends RuntimeException {
    public BookingStateNotFoundException(String e) {
        super(e);
    }
}
