package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.add(itemRequestCreateDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getId(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByRequesterId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getPageAllByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        return itemRequestService.getPageAllByRequestId(userId, from, size);
    }
}
