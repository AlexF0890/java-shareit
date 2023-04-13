package ru.practicum.shareit.bookings;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingDtoCreate bookingDtoCreate, Long userId);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByOwnerId(String state, Long userId, Integer from, Integer size);

    List<BookingDto> getAllByBookerId(String state, Long userId, Integer from, Integer size);

    BookingDto updateStatus(Long bookingId, Boolean approved, Long userId);
}
