package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    RequestItemDto add(RequestItemDto itemDto, Long userId);

    void delete(Long itemId);

    RequestItemDto update(RequestItemDto itemDto, Long itemId, Long userId);

    ItemDto getId(Long itemId, Long userId);

    List<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Long userId, Integer from, Integer size);
}
