package ru.practicum.shareit.itemtest.itemrequesttest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestTest {
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
    }

    @Test
    void userTest() {
        Long id = 1L;
        String description = "description";
        User user = new User();
        LocalDateTime time = LocalDateTime.now();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequester(user);
        itemRequest.setCreated(time);

        assertAll("User не заведен",
                () -> assertEquals(itemRequest.getId(), 1L),
                () -> assertEquals(itemRequest.getDescription(), "description"),
                () -> assertEquals(itemRequest.getRequester(), user),
                () -> assertEquals(itemRequest.getCreated(), time)
        );
    }
}
