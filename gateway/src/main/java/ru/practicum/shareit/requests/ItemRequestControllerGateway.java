package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestControllerGateway {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody ItemRequestDtoGateway itemRequestCreateDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.add(itemRequestCreateDto, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable(name = "requestId") Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getId(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequesterId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getPageAllByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getPageAllByRequestId(userId, from, size);
    }
}
