package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserEmailCheckException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        checkUserEmail(userDto.getEmail());
        User user = userRepository.addUser(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.getUserId(id) != null) {
            userRepository.deleteUser(id);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserId(Long id) {
        if (userRepository.getUserId(id) != null) {
            User user = userRepository.getUserId(id);
            return userMapper.toUserDto(user);
        } else {
            log.error("Пользователя не существует");
            throw new UserNotFoundException("Пользователя не существует");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        checkUserEmail(userDto.getEmail());

        if (userRepository.getUserId(id) != null) {
            User user = userRepository.getUserId(id);

            if (userDto.getName()!=null) {
                user.setName(userDto.getName());
            }

            if (userDto.getEmail()!= null) {
                user.setEmail(userDto.getEmail());
            }

            User userUpdate = userRepository.updateUser(user);
            return userMapper.toUserDto(userUpdate);
        } else {
            log.error("Пользователя не существует");
            throw new UserNotFoundException("Пользователя не существует");
        }
    }

    private void checkUserEmail(String email) {
        boolean isCheckEmail = userRepository.getAllUsers().stream()
                .map(User::getEmail)
                .noneMatch(usersEmail -> usersEmail.equals(email));
        if (!isCheckEmail) {
            throw new UserEmailCheckException(email + " уже существует");
        }
    }
}
