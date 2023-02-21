package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(ItemRequestCreateDto itemRequestCreateDto, Long userId);

    ItemRequestDto getId(Long itemRequestId, Long userId);

    List<ItemRequestDto> getAllByRequesterId(Long userId);

    List<ItemRequestDto> getPageAllByRequestId(Long userId, Integer from, Integer size);
}
