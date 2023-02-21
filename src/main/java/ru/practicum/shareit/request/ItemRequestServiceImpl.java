package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotNullDescriptionException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService{
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto add(ItemRequestCreateDto itemRequestCreateDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
        if (itemRequestCreateDto.getDescription() == null) {
            log.error("Должен иметь описание");
            throw new ItemRequestNotNullDescriptionException("Должен иметь описание");
        }
        ItemRequest itemRequest = itemRequestMapper.toItemRequestCreateDto(itemRequestCreateDto, user);
        itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    @Transactional
    public ItemRequestDto getId(Long itemRequestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Записи не существует"));
        List<Item> items = itemRepository.findAllByRequestId(itemRequestId);
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        return getItemRequestDto(itemRequestDto, items);
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getAllByRequesterId(Long requesterId) {
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
        List<ItemRequestDto> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId())
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .toList();
        List<Long> requestIdList = itemRequests.stream().map(ItemRequestDto::getId).collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIdList);
        return itemRequests.stream()
                .map(itemRequest -> getItemRequestDto(itemRequest, items))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getPageAllByRequestId(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
        List<ItemRequestDto> itemRequestList;
        if (from == null && size == null) {
            itemRequestList = Collections.emptyList();
            return itemRequestList;
        } else {
            Pageable page = PageRequest.of(from, size);
            itemRequestList = itemRequestRepository.findAllByRequesterIdIsNot(userId, page).stream()
                    .map(itemRequestMapper::toItemRequestDto)
                    .collect(toList());
            List<Long> requestIdList = itemRequestList.stream()
                    .map(ItemRequestDto::getId)
                    .collect(toList());
            List<Item> items = itemRepository.findAllByRequestIdIn(requestIdList);
            return itemRequestList.stream()
                    .map(itemRequestDto -> getItemRequestDto(itemRequestDto, items))
                    .collect(toList());
        }
    }

    private ItemRequestDto getItemRequestDto(ItemRequestDto itemRequestDto, List<Item> items) {
        List<RequestItemDto> requestItemDtoList = items.stream()
                .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                .map((Item item1) -> itemMapper.toRequestItemDtoList(item1, itemRequestDto.getId()))
                .collect(toList());
        itemRequestDto.setItems(requestItemDtoList);
        return itemRequestDto;
    }
}
