package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDtoCreation commentDtoCreation) {
        if (!StringUtils.hasText(commentDtoCreation.getText())) {
            throw new CommentTextIsEmptyException("Текс не может быть пустым");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CommentItemNotFoundException("Данной вещи не существует"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
        bookingRepository.findByItemId(userId, itemId)
                .orElseThrow(() -> new BookingCommentItemIdException("Такой записи не существует"));
        Comment comment = commentMapper.toComment(commentDtoCreation, item, user);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }
}
