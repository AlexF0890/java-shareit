package ru.practicum.shareit.usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void userTest() {
        Long id = 1L;
        String name = "name";
        String email = "email@mail.ru";

        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        assertAll("User не заведен",
                () -> assertEquals(user.getId(), 1L),
                () -> assertEquals(user.getName(), "name"),
                () -> assertEquals(user.getEmail(), "email@mail.ru")
        );
    }
}
