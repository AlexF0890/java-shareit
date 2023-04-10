package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public ItemRequest toItemRequestCreateDto(ItemRequestCreateDto itemRequestCreateDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestCreateDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public ItemRequestDto toItemRequestDtoList(ItemRequest itemRequest, List<Item> items) {
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);

        itemRequestDto.setItems(itemMapper.toListRequestItemDto(items));

        return itemRequestDto;
    }
}
