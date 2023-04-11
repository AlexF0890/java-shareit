package ru.practicum.shareit.requests;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDtoGateway {
    @NotNull
    private String description;
}
