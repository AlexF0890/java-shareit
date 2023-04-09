package ru.practicum.shareit.bookings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.items.ItemBookingDto;
import ru.practicum.shareit.items.ItemMapper;
import ru.practicum.shareit.items.Item;
import ru.practicum.shareit.users.User;
import ru.practicum.shareit.users.UserMapper;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public Booking toBooking(BookingDtoCreate bookingDtoCreate, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoCreate.getStart().truncatedTo(ChronoUnit.SECONDS));
        booking.setEnd(bookingDtoCreate.getEnd().truncatedTo(ChronoUnit.SECONDS));
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart().truncatedTo(ChronoUnit.SECONDS));
        bookingDto.setEnd(booking.getEnd().truncatedTo(ChronoUnit.SECONDS));
        bookingDto.setBooker(userMapper.toUserDto(booking.getBooker()));
        bookingDto.setItem(itemMapper.toItemDto(booking.getItem()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static ItemBookingDto toItemBookingDto(Booking booking) {
        ItemBookingDto itemBookingDto = new ItemBookingDto();
        itemBookingDto.setId(booking.getId());
        itemBookingDto.setBookerId(booking.getBooker().getId());
        itemBookingDto.setStart(booking.getStart().truncatedTo(ChronoUnit.SECONDS));
        itemBookingDto.setEnd(booking.getEnd().truncatedTo(ChronoUnit.SECONDS));
        return itemBookingDto;
    }

    public List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        return bookings.stream().map(this::toBookingDto).collect(Collectors.toList());
    }
}
