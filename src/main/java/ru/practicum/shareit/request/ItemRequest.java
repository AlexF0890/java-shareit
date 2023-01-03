package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
public class ItemRequest {
    private Long id;
    @NotNull
    private String description;
    @NotNull
    private User requestor;
    private LocalDateTime created;
}
