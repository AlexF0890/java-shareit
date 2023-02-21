package ru.practicum.shareit.item;

public interface CommentService {
    CommentDto add(Long itemId, Long userId, CommentDtoCreation commentDtoCreation);
}
