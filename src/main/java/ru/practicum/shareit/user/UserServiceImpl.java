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
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto add(UserDto userDto) {
        if (userRepository.checkEmail(userDto.getEmail())) {
            User user = userRepository.add(userMapper.toUser(userDto));
            return userMapper.toUserDto(user);
        } else {
            log.error("Пользователя не существует");
            throw new UserEmailCheckException("Пользователя не существует");
        }
    }

    @Override
    public void delete(Long id) {
        if (userRepository.isExist(id)) {
            userRepository.delete(id);
        }
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getId(Long id) {
        if (userRepository.isExist(id)) {
            User user = userRepository.getId(id);
            return userMapper.toUserDto(user);
        } else {
            log.error("Пользователя не существует");
            throw new UserNotFoundException("Пользователя не существует");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        if (userRepository.checkEmail(userDto.getEmail())) {
            if (userRepository.isExist(id)) {
                User user = userRepository.getId(id);

                if (userDto.getName() != null) {
                    user.setName(userDto.getName());
                }

                if (userDto.getEmail() != null) {
                    user.setEmail(userDto.getEmail());
                }

                User userUpdate = userRepository.update(user);
                return userMapper.toUserDto(userUpdate);
            } else {
                log.error("Пользователя не существует");
                throw new UserNotFoundException("Пользователя не существует");
            }
        } else {
            log.error("Email уже существует");
            throw new UserEmailCheckException("Email уже существует");
        }
    }
}
