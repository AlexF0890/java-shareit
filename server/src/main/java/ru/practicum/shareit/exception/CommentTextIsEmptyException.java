package ru.practicum.shareit.exception;

public class CommentTextIsEmptyException extends RuntimeException {
    public CommentTextIsEmptyException(String e) {
        super(e);
    }
}
