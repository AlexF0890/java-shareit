package ru.practicum.shareit.exception;

public class ItemNotAvailableException extends RuntimeException{
    public ItemNotAvailableException(String e){
        super(e);
    }
}
