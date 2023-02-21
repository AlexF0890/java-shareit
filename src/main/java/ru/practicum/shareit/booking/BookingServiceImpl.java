package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<BookingDto> getAllByBookerId(String string, Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        List<BookingDto> bookingDtoList;
        if (from == null && size == null) {
            bookingDtoList = getAllByBookerIdList(string, userId);
        } else {
            bookingDtoList = getAllByBookerIdListPage(string, userId, from, size);
        }
        return bookingDtoList;
    }

    @Override
    public List<BookingDto> getAllByOwnerId(String string, Long ownerId, Integer from, Integer size) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        List<BookingDto> bookingDtoList;
        if (from == null && size == null) {
            bookingDtoList = getAllByOwnerIdList(string, ownerId);
        } else {
            bookingDtoList = getAllByOwnerIdListPage(string, ownerId, from, size);
        }
        return bookingDtoList;
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

    private List<BookingDto> getAllByOwnerIdList(String string, Long ownerId) {
        STATE state;
        try {
            state = STATE.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + string);
        }
        return switch (state) {
            case ALL -> bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerId(
                    ownerId));
            case WAITING -> bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerIdAndStatusEquals(
                    ownerId, STATUS.WAITING));
            case REJECTED -> bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerIdAndStatusEquals(
                    ownerId, STATUS.REJECTED));
            case FUTURE -> bookingMapper.toBookingDtoList(bookingRepository
                    .findByItemOwnerIdAndStartDateIsAfter(ownerId));
            case PAST -> bookingMapper.toBookingDtoList(bookingRepository
                    .findByItemOwnerIdAndEndDateIsBefore(ownerId));
            case CURRENT -> bookingMapper.toBookingDtoList(bookingRepository
                    .findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(ownerId));
            default -> throw new BookingStateNotFoundException("Не существует");
        };
    }

    private List<BookingDto> getAllByOwnerIdListPage(String string, Long ownerId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        STATE state;
        try {
            state = STATE.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + string);
        }
        return switch (state) {
            case ALL -> bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerId(
                    ownerId, page).toList());
            case WAITING -> bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerIdAndStatusEquals(
                    ownerId, STATUS.WAITING, page).toList());
            case REJECTED -> bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerIdAndStatusEquals(
                    ownerId, STATUS.REJECTED, page).toList());
            case FUTURE -> bookingMapper.toBookingDtoList(bookingRepository
                    .findByItemOwnerIdAndStartDateIsAfter(ownerId, page).toList());
            case PAST -> bookingMapper.toBookingDtoList(bookingRepository
                    .findByItemOwnerIdAndEndDateIsBefore(ownerId, page).toList());
            case CURRENT -> bookingMapper.toBookingDtoList(bookingRepository
                    .findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(ownerId, page).toList());
            default -> throw new BookingStateNotFoundException("Не существует");
        };
    }

    private List<BookingDto> getAllByBookerIdListPage(String string, Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        Pageable page = PageRequest.of(from, size);
        STATE state;
        try {
            state = STATE.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + string);
        }
        return switch (state) {
            case ALL -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdOrderByBookerId(page).toList());
            case PAST -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndEndDateIsBefore(userId, page).toList());
            case FUTURE -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndStartDateIsAfter(userId, page).toList());
            case WAITING -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.WAITING, page).toList());
            case CURRENT -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId, page).toList());
            case REJECTED -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.REJECTED, page).toList());
            default -> throw new BookingStateNotFoundException("Не существует");
        };
    }

    private List<BookingDto> getAllByBookerIdList(String string, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }

        STATE state;
        try {
            state = STATE.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + string);
        }
        return switch (state) {
            case ALL -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdOrderByBookerId(userId));
            case PAST -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndEndDateIsBefore(userId));
            case FUTURE -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndStartDateIsAfter(userId));
            case WAITING -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.WAITING));
            case CURRENT -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId));
            case REJECTED -> bookingMapper.toBookingDtoList(
                    bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.REJECTED));
            default -> throw new BookingStateNotFoundException("Не существует");
        };
    }
}
