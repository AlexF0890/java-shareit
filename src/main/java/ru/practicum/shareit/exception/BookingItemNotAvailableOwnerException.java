package ru.practicum.shareit.exception;

public class BookingItemNotAvailableOwnerException extends RuntimeException {
    public BookingItemNotAvailableOwnerException(String e) {
        super(e);
    }
}
