package ru.practicum.shareit.usertest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    void testGetAll() {
        User userOne = new User(1L, "one", "one@mail.ru");
        User userTwo = new User(2L, "two", "two@mail.ru");
        List<User> users = List.of(userOne, userTwo);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtoList = userMapper.toUserDtoList(users);
        List<UserDto> userDtoListService = userService.getAll();

        assertEquals(userDtoList, userDtoListService, "Fail");

        verify(userRepository).findAll();
    }

    @Test
    void getExistByIdTest() {
        Long id = 100L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getId(id));

        assertEquals(exception.getMessage(), "Пользователя не существует");
    }

    @Test
    void addUserTest() {
        User userOne = new User(1L, "one", "one@mail.ru");
        UserDto userDto = new UserDto(1L, "one", "one@mail.ru");
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto userDto1 = userMapper.toUserDto(userOne);
        when(userMapper.toUser(any(UserDto.class))).thenReturn(userOne);

        User userThree1 = userMapper.toUser(userDto);
        when(userRepository.save(any(User.class))).thenReturn(userThree1);

        UserDto userDto2 = userService.add(userDto1);

        assertAll("Fail",
                () -> assertEquals(userDto2.getId(), userDto1.getId()),
                () -> assertEquals(userDto2.getName(), userDto1.getName()),
                () -> assertEquals(userDto2.getEmail(), userDto1.getEmail())
        );

        assertAll("Fail",
                () -> assertEquals(userOne.getId(), userThree1.getId()),
                () -> assertEquals(userOne.getName(), userThree1.getName()),
                () -> assertEquals(userOne.getEmail(), userThree1.getEmail())
        );
    }

    @Test
    void updateUserTest() {
            Long userId = 1L;
            UserDto userDto = UserDto.builder()
                    .email("update@email.ru")
                    .name("updateName")
                    .build();

            User user = new User();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserDto expectedUserDto = UserDto.builder()
                    .email("update@email.ru")
                    .name("updateName")
                    .build();

            when(userMapper.toUserDto(user)).thenReturn(expectedUserDto);

            UserDto actualUserDto = userService.update(userDto, userId);

            assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void delete() {
        Long id = 1L;
        userService.delete(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void updateUserTestNullEmail() {
        Long userId = 1L;
        UserDto userDtoForUpdate = UserDto.builder()
                .name("updateName")
                .build();

        User oldUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        UserDto expectedUserDto = UserDto.builder()
                .email("date@mail.ru")
                .name("updateName")
                .build();

        when(userMapper.toUserDto(oldUser)).thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.update(userDtoForUpdate, userId);

        assertEquals("updateName", actualUserDto.getName());
        assertEquals("date@mail.ru", actualUserDto.getEmail());
    }

    @Test
    void getByIdTest() {
        Long id = 1L;
        User user = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto userDto1 = userMapper.toUserDto(user);
        UserDto userDto = userService.getId(id);

        assertEquals(userDto, userDto1);
    }
}
