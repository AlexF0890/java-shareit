package ru.practicum.shareit.items;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDtoGateway {
    @NotNull
    private String text;
}
