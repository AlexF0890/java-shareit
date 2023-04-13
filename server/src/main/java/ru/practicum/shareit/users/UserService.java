package ru.practicum.shareit.users;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    void delete(Long id);

    List<UserDto> getAll();

    UserDto getId(Long id);

    UserDto update(Long id, UserDto userDto);
}
