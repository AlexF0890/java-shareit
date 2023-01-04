package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        if (!userRepository.isExist(userId)) {
            log.error("Пользователя не существует");
            throw new UserNotFoundException("Пользователя не существует");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Предмет должен иметь статус");
            throw new ItemNotAvailableException("Предмет должен иметь статус");
        }
        if (itemDto.getName().isEmpty()) {
            log.error("Предмет должен иметь имя");
            throw new ItemNotNullNameException("Предмет должен иметь имя");
        }

        if (itemDto.getDescription() == null) {
            log.error("Предмет должен иметь описание");
            throw new ItemNotNullDescriptionException("Предмет должен иметь описание");
        }

        Item item = itemRepository.add(itemMapper.toItem(itemDto, userId));
        return itemMapper.toItemDto(item);
    }

    @Override
    public void delete(Long id) {
        if (itemRepository.isExist(id)) {
            itemRepository.delete(id);
        } else {
            log.error("Данной вещи не существует");
            throw new ItemNotFoundException("Данной вещи не существует");
        }
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        if (!itemRepository.isExist(id)) {
            log.error("Данной вещи не существует");
            throw new ItemNotFoundException("Данной вещи не существует");
        }

        if (!userRepository.isExist(userId)) {
            log.error("Пользователя не существует");
            throw new UserNotFoundException("Пользователя не существует");
        }

        Item item = itemRepository.getId(id);

        if (!item.getUser().getId().equals(userId)) {
            log.error("Пользователю не принадлежит данная вещь");
            throw new UserNotBelongsItemException("Пользователю не принадлежит данная вещь");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.update(item));
    }

    @Override
    public ItemDto getId(Long id) {
        if (itemRepository.isExist(id)) {
            Item item = itemRepository.getId(id);
            return itemMapper.toItemDto(item);
        } else {
            log.error("Данной вещи не существует");
            throw new ItemNotFoundException("Данной вещи не существует");
        }
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        return itemRepository.getItemsByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String search) {
        if (StringUtils.isEmpty(search)) {
            return new ArrayList<>();
        }

        return itemRepository.getAll().stream()
                .filter(Item::getAvailable)
                .filter(itemDto -> StringUtils.startsWithIgnoreCase(itemDto.getName(), search)
                || StringUtils.startsWithIgnoreCase(itemDto.getDescription(), search)
                || StringUtils.endsWithIgnoreCase(itemDto.getName(), search)
                || StringUtils.endsWithIgnoreCase(itemDto.getDescription(), search))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
