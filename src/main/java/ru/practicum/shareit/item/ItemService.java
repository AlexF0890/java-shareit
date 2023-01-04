package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long userId);

    void delete(Long id);

    ItemDto update(ItemDto itemDto, Long id, Long userId);

    ItemDto getId(Long id);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> search(String text);
}
