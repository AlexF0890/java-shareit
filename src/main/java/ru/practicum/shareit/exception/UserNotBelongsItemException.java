package ru.practicum.shareit.exception;

public class UserNotBelongsItemException extends RuntimeException{
    public UserNotBelongsItemException(String e){
        super(e);
    }
}
