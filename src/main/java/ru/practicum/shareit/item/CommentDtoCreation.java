package ru.practicum.shareit.item;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDtoCreation {
    @NotNull
    private String text;
}
