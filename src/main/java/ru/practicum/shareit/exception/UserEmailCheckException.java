package ru.practicum.shareit.exception;

public class UserEmailCheckException extends RuntimeException {
    public UserEmailCheckException(String e) {
        super(e);
    }
}
