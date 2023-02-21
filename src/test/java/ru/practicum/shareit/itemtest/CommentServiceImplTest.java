package ru.practicum.shareit.itemtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.STATUS;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    CommentMapper commentMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    void addTest() {
        Long userId = 1L;
        User user = User.builder().id(userId)
                .email("update@email.ru").name("update")
                .build();
        Long userId2 = 2L;
        User user2 = User.builder().id(userId2)
                .email("update2@email.ru").name("update2")
                .build();
        Long id = 1L;
        Item item = new Item(id, "item", "description", true, user, null);

        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
        lenient().when(userMapper.toUserDto(user)).thenReturn(new UserDto());

        UserDto userDto = userMapper.toUserDto(user);
        userDto.setName(user.getName());
        Long commentId = 1L;
        CommentDtoCreation commentDtoCreation = new CommentDtoCreation("comment");

        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .authorName(userDto.getName())
                .created(LocalDateTime.now())
                .text("comment")
                .build();

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Long bookingId = 1L;
        Booking booking = Booking.builder().id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(STATUS.APPROVED)
                .booker(user).item(item)
                .build();

        when(bookingRepository.findByItemId(id, userId)).thenReturn(Optional.of(booking));
        when(commentMapper.toComment(commentDtoCreation, item, user)).thenReturn(new Comment());

        Comment comment = commentMapper.toComment(commentDtoCreation, item, user);
        comment.setAuthor(user);
        comment.setItem(item);

        when(commentService.add(id, userId, commentDtoCreation)).thenReturn(commentDto);

        CommentDto commentDto1 = commentMapper.toCommentDto(comment);
        CommentDto commentDto2 = commentService.add(id, userId, commentDtoCreation);

        assertEquals(commentDto1, commentDto2);
    }
}
