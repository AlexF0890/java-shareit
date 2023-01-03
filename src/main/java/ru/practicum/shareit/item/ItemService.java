package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    void deleteItem(Long id);

    ItemDto updateItem(ItemDto itemDto, Long id, Long userId);

    ItemDto getItemId(Long id);

    List<ItemDto> getItemsUserId(Long userId);

    List<ItemDto> searchItems(String text);
}
