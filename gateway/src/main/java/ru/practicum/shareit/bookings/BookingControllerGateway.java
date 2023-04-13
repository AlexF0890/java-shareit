package ru.practicum.shareit.bookings;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BookingStateIncorectException;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingControllerGateway {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody BookingDtoGateway bookingDtoCreate,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.add(bookingDtoCreate, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long bookingId,
                                               @RequestParam Boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.updateStatus(bookingId, approved, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBookerId(
            @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10") Integer size) {
        String bookingState = BookingState.from(state).orElseThrow(
                    () -> new BookingStateIncorectException("Unknown state: " + state));
        return bookingClient.getAllByBookerId(bookingState, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByBookerItems(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        String bookingState = BookingState.from(state).orElseThrow(
                    () -> new BookingStateIncorectException("Unknown state: " + state));
        return bookingClient.getAllByOwnerId(bookingState, ownerId, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }
}
