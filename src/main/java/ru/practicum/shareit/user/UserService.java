package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);
    void deleteUser(Long id);
    List<UserDto> getAllUsers();
    UserDto getUserId(Long id);
    UserDto updateUser(UserDto userDto, Long id);
}
