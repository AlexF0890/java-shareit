package ru.practicum.shareit.usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserDto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDtoTest {
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
    }

    @Test
    void userDtoAddTest() {
        Long id = 1L;
        String name = "one";
        String email = "one@mail.ru";
        userDto.setId(id);
        userDto.setName(name);
        userDto.setEmail(email);

        assertAll("Fail",
                () -> assertEquals(userDto.getId(), id),
                () -> assertEquals(userDto.getName(), name),
                () -> assertEquals(userDto.getEmail(), email)
        );
    }
}
