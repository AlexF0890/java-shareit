package ru.practicum.shareit.items;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public RequestItemDto add(@RequestBody RequestItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public RequestItemDto update(@RequestBody RequestItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getId(@PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getId(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(defaultValue = "0") Integer from,
                                @RequestParam(defaultValue = "10") Integer size) {
        return itemService.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDtoCreation comment) {
        return itemService.addComment(itemId, userId, comment);
    }
}
