package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestCreateDto {
    @NotNull
    private String description;
}
