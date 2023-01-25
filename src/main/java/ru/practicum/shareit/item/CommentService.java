package ru.practicum.shareit.item;

public interface CommentService {
    CommentDto addComment(Long itemId, Long userId, CommentDtoCreation commentDtoCreation);
}
