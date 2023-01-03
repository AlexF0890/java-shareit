package ru.practicum.shareit.item;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
}
