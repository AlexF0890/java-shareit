package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
public class Item {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private User user = new User();
    private ItemRequest request;
}
