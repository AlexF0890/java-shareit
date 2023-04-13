package ru.practicum.shareit.users;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoGateway {
    private Long id;
    private String name;
    private String email;
}
