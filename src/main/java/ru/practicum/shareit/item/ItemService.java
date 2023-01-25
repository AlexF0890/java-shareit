package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long userId);

    void delete(Long itemId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getId(Long itemId, Long userId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> search(String text, Long userId);
}
