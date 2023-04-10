package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public Comment toComment(CommentDtoCreation commentDtoCreation, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(commentDtoCreation.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public List<CommentDto> toListCommentDto(List<Comment> comments) {
        return comments.stream().map(this::toCommentDto).collect(Collectors.toList());
    }
}
