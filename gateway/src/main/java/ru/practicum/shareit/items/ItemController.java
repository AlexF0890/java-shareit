package ru.practicum.shareit.items;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody RequestItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody RequestItemDto itemDto, @PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable Long itemId) {
        return itemClient.delete(itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getId(@PathVariable Long itemId,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getId(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(name = "text") String text,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody CommentDtoCreation comment) {
        return itemClient.addComment(itemId, userId, comment);
    }
}
