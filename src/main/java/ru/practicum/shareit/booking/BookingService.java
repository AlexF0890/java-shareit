package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingDtoCreate bookingDtoCreate, Long userId);
    BookingDto getById(Long userId, Long bookingId);
    List<BookingDto> getAllByOwnerId(String state, Long userId);
    List<BookingDto> getAllByBookerId(String state, Long userId);
    BookingDto updateStatus(Long bookingId, Boolean approved, Long userId);
}
