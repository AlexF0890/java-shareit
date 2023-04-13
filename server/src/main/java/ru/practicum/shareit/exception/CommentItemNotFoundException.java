package ru.practicum.shareit.exception;

public class CommentItemNotFoundException extends RuntimeException {
    public CommentItemNotFoundException(String e) {
        super(e);
    }
}
