package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-controllers.
*/

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemId(@PathVariable Long itemId) {
        return itemService.getId(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.search(text);
    }
}
