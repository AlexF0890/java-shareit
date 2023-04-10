package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.RequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@ToString
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<RequestItemDto> items;
}
