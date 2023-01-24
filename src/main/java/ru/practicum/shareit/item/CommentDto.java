package ru.practicum.shareit.item;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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
