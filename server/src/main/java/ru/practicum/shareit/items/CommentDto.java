package ru.practicum.shareit.items;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotNull
    @NotBlank
    private String text;

    @NotNull
    private String authorName;

    @NotNull
    private LocalDateTime created;
}
