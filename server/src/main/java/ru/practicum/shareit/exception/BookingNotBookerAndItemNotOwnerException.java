package ru.practicum.shareit.exception;

public class BookingNotBookerAndItemNotOwnerException extends RuntimeException {
    public BookingNotBookerAndItemNotOwnerException(String e) {
        super(e);
    }
}

