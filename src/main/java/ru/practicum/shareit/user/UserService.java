package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    void delete(Long id);

    List<UserDto> getAll();

    UserDto getId(Long id);

    UserDto update(UserDto userDto, Long id);
}
