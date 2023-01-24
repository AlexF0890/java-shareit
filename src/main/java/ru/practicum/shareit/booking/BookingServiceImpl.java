package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto add(BookingDtoCreate bookingDtoCreate, Long userId) {
        Item item = itemRepository.findById(bookingDtoCreate.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Такого предмета не существует"));
        if (item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователь предмета не может брать свой предмет");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Предмет отсутствует");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя не существует"));
        if (bookingDtoCreate.getStart().isAfter(bookingDtoCreate.getEnd())
                || bookingDtoCreate.getStart().isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))) {
            throw new BookingStartTimeAfterEndTimeException("Время начала аренды не может быть после даты конца");
        }
        Booking booking = bookingMapper.toBooking(bookingDtoCreate, user, item);
        booking.setStatus(STATUS.WAITING);
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
         Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Такой записи не существует"));
         if (!booking.getItem().getOwner().getId().equals(userId) &&
                 !booking.getBooker().getId().equals(userId)) {
             throw new BookingNotBookerAndItemNotOwnerException("Запись пренадлежит не этому пользователю или предмет не его");
         }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBookerId(String string, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }

        STATE state;
        try {
            state = STATE.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + string);
        }
        switch (state) {
            case ALL:
                return bookingMapper.toBookingDtoList(
                        bookingRepository.findBookingByBookerIdOrderByStartDateDescOrderByDesc(userId));
            case PAST:
                return bookingMapper.toBookingDtoList(
                        bookingRepository.findBookingByBookerIdAndEndDateIsBeforeOrderByDesc(userId));
            case FUTURE:
                return bookingMapper.toBookingDtoList(
                        bookingRepository.findBookingsByBookerIdAndStartDateIsAfterOrderByStartDateDesc(userId));
            case WAITING:
                return bookingMapper.toBookingDtoList(
                        bookingRepository.findBookingsByBookerIdAndStatusEquals(userId, STATUS.WAITING));
            case CURRENT:
                return bookingMapper.toBookingDtoList(
                    bookingRepository.findBookingsByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId));
            case REJECTED:
                return bookingMapper.toBookingDtoList(
                        bookingRepository.findBookingsByBookerIdAndStatusEquals(userId, STATUS.REJECTED));
            default:
                throw new BookingStateNotFoundException("Не существует");
        }
    }

    @Override
    public List<BookingDto> getAllByOwnerId(String string, Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        STATE state;
        try {
            state = STATE.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + string);
        }
        switch (state) {
            case ALL:
                return bookingMapper.toBookingDtoList(bookingRepository.findBookingsByItemOwnerIdOrderByStartDateDesc(
                        ownerId));
            case WAITING:
                return bookingMapper.toBookingDtoList(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(
                        ownerId, STATUS.WAITING));
            case REJECTED:
                return bookingMapper.toBookingDtoList(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(
                        ownerId, STATUS.REJECTED));
            case FUTURE:
                return bookingMapper.toBookingDtoList(bookingRepository.
                        findBookingsByItemOwnerIdAndStartDateIsAfterOrderByStartDateDesc(ownerId));
            case PAST:
                return bookingMapper.toBookingDtoList(bookingRepository.
                        findBookingsByItemOwnerIdAndEndDateIsBefore(ownerId));
            case CURRENT:
                return bookingMapper.toBookingDtoList(bookingRepository.
                        findBookingsByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(ownerId));
            default:
                throw new BookingStateNotFoundException("Не существует");
        }
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Такой записи не существует"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
           throw new BookingItemNotAvailableOwnerException("Пользователю не пренадлежит этот предмет");
        }

        if (booking.getStatus().equals(STATUS.APPROVED)) {
            throw new BookingChangeStatusApprovedException("Бронирование уже совершено");
        }

        booking.setStatus((approved) ? (STATUS.APPROVED) : (STATUS.REJECTED));
        return bookingMapper.toBookingDto(booking);
    }
}
