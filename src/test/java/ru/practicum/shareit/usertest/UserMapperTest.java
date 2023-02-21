package ru.practicum.shareit.usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testUserConvertUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("one");
        user.setEmail("email@mail.ru");
        UserDto userDto = userMapper.toUserDto(user);

        assertAll("Fail",
                () -> assertEquals(userDto.getId(), user.getId()),
                () -> assertEquals(userDto.getName(), user.getName()),
                () -> assertEquals(userDto.getEmail(), user.getEmail())
        );
    }

    @Test
    void testUserDtoConvertUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("one");
        userDto.setEmail("email@mail.ru");
        User user = userMapper.toUser(userDto);

        assertAll("Fail",
                () -> assertEquals(user.getId(), userDto.getId()),
                () -> assertEquals(user.getName(), userDto.getName()),
                () -> assertEquals(user.getEmail(), userDto.getEmail())
        );
    }
}
