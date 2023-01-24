package ru.practicum.shareit.item;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ItemBookingDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
