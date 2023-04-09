package ru.practicum.shareit.items;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
@ToString
@AllArgsConstructor
@Builder
public class ItemBookingDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
