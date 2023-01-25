package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserEmailNotNull;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        if (userDto.getEmail() != null && userDto.getEmail().contains("@")) {
            return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
        } else {
            throw new UserEmailNotNull("Почта не должна быть пустой или должна содержать @");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    public UserDto getId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return userMapper.toUserDto(user);
    }
}
