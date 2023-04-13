package ru.practicum.shareit.items;

import lombok.*;

@Data
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoGateway {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
