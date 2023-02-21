package ru.practicum.shareit.itemtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemTest {
    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
    }

    @Test
    void userTest() {
        Long id = 1L;
        String name = "name";
        String description = "description";
        Boolean available = true;
        User userOne = new User(1L, "userOne", "userOne@mail.ru");
        User userTwo = new User(2L, "userTwo", "userTwo@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description", userTwo,
                LocalDateTime.of(12, 12, 12, 12, 12));

        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(userOne);
        item.setRequest(itemRequest);

        assertAll("Item не заведен",
                () -> assertEquals(item.getId(), 1L),
                () -> assertEquals(item.getName(), "name"),
                () -> assertEquals(item.getAvailable(), true),
                () -> assertEquals(item.getOwner(), userOne),
                () -> assertEquals(item.getRequest(), itemRequest)
        );
    }
}
