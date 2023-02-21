package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public Item toItem(RequestItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public RequestItemDto toRequestItemDto(Item item) {
        RequestItemDto requestItemDto = new RequestItemDto();
        requestItemDto.setId(item.getId());
        requestItemDto.setName(item.getName());
        requestItemDto.setDescription(item.getDescription());
        requestItemDto.setAvailable(item.getAvailable());
        return requestItemDto;
    }

    public RequestItemDto toRequestItemDtoList(Item item, Long requestId) {
        RequestItemDto requestItemDto = new RequestItemDto();
        requestItemDto.setId(item.getId());
        requestItemDto.setName(item.getName());
        requestItemDto.setDescription(item.getDescription());
        requestItemDto.setAvailable(item.getAvailable());
        requestItemDto.setRequestId(requestId);
        return requestItemDto;
    }

    public List<ItemDto> toListItemDto(List<Item> items) {
        return items.stream().map(this::toItemDto).collect(Collectors.toList());
    }

    public List<RequestItemDto> toListRequestItemDto(List<Item> items) {
        return items.stream().map(this::toRequestItemDto).collect(Collectors.toList());
    }
}
