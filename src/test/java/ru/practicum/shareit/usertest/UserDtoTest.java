package ru.practicum.shareit.usertest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserDto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDtoTest {
    @Test
    void UserDtoTest() {
        Long id = 1L;
        String name = "one";
        String email = "one@mail.ru";

        UserDto userDto = new UserDto();
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
