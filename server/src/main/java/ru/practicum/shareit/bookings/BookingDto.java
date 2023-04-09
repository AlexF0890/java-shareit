package ru.practicum.shareit.bookings;

import lombok.*;
import ru.practicum.shareit.items.ItemDto;
import ru.practicum.shareit.users.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
@ToString
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private UserDto booker;

    @NotNull
    private ItemDto item;

    @NotNull
    private STATUS status;
}
