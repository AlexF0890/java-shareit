package ru.practicum.shareit.exception;

public class UserEmailNotNull extends RuntimeException {
    public UserEmailNotNull(String e) {
        super(e);
    }
}
