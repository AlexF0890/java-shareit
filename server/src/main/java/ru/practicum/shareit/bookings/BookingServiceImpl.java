package ru.practicum.shareit.bookings;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.items.Item;
import ru.practicum.shareit.items.ItemRepository;
import ru.practicum.shareit.users.User;
import ru.practicum.shareit.users.UserRepository;

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
        return bookingCheck(userId, booking);
    }

    @Override
    public List<BookingDto> getAllByBookerId(String state, Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        List<BookingDto> bookingDtoList;
        if (from == null && size == null) {
            bookingDtoList = getAllByBookerIdList(state, userId);
        } else {
            bookingDtoList = getAllByBookerIdListPage(state, userId, from, size);
        }
        return bookingDtoList;
    }

    @Override
    public List<BookingDto> getAllByOwnerId(String state, Long ownerId, Integer from, Integer size) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        List<BookingDto> bookingDtoList;
        if (from == null && size == null) {
            bookingDtoList = getAllByOwnerIdList(state, ownerId);
        } else {
            bookingDtoList = getAllByOwnerIdListPage(state, ownerId, from, size);
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

    private List<BookingDto> getAllByOwnerIdList(String state, Long ownerId) {
        try {
            switch (STATE.valueOf(state)) {
                case ALL:
                    return bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerId(
                            ownerId));
                case WAITING:
                    return bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerIdAndStatusEquals(
                            ownerId, STATUS.WAITING));
                case REJECTED:
                    return bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerIdAndStatusEquals(
                            ownerId, STATUS.REJECTED));
                case FUTURE:
                    return bookingMapper.toBookingDtoList(bookingRepository
                            .findByItemOwnerIdAndStartDateIsAfter(ownerId));
                case PAST:
                    return bookingMapper.toBookingDtoList(bookingRepository
                            .findByItemOwnerIdAndEndDateIsBefore(ownerId));
                case CURRENT:
                    return bookingMapper.toBookingDtoList(bookingRepository
                            .findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(ownerId));
                default:
                    throw new BookingStateNotFoundException("Не существует");
            }
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + state);
        }
    }

    private List<BookingDto> getAllByOwnerIdListPage(String state, Long ownerId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "end"));
        try {
            switch (STATE.valueOf(state)) {
                case ALL:
                    return bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerId(
                            ownerId, page).toList());
                case WAITING:
                    return bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerIdAndStatusEquals(
                            ownerId, STATUS.WAITING, page).toList());
                case REJECTED:
                    return bookingMapper.toBookingDtoList(bookingRepository.findByItemOwnerIdAndStatusEquals(
                            ownerId, STATUS.REJECTED, page).toList());
                case FUTURE:
                    return bookingMapper.toBookingDtoList(bookingRepository
                            .findByItemOwnerIdAndStartDateIsAfter(ownerId, page).toList());
                case PAST:
                    return bookingMapper.toBookingDtoList(bookingRepository
                            .findByItemOwnerIdAndEndDateIsBefore(ownerId, page).toList());
                case CURRENT:
                    return bookingMapper.toBookingDtoList(bookingRepository
                            .findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(ownerId, page).toList());
                default:
                    throw new BookingStateNotFoundException("Не существует");
            }
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + state);
        }
    }

    private List<BookingDto> getAllByBookerIdListPage(String state, Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "end"));
        try {
            switch (STATE.valueOf(state)) {
                case ALL:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerId(page).toList());
                case PAST:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndEndDateIsBefore(userId, page).toList());
                case FUTURE:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndStartDateIsAfter(userId, page).toList());
                case WAITING:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.WAITING, page).toList());
                case CURRENT:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId, page).toList());
                case REJECTED:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.REJECTED, page).toList());
                default: throw new BookingStateNotFoundException("Не существует");
            }
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + state);
        }
    }

    private List<BookingDto> getAllByBookerIdList(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя не существует");
        }
        try {
            switch (STATE.valueOf(state)) {
                case ALL:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerId(userId));
                case PAST:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndEndDateIsBefore(userId));
                case FUTURE:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndStartDateIsAfter(userId));
                case WAITING:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.WAITING));
                case CURRENT:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId));
                case REJECTED:
                    return bookingMapper.toBookingDtoList(
                            bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.REJECTED));
                default:
                    throw new BookingStateNotFoundException("Не существует");
            }
        } catch (IllegalArgumentException e) {
            throw new BookingStateNotFoundException("Unknown state: " + state);
        }
    }

    private BookingDto bookingCheck(Long userId, Booking booking) {
        if (!booking.getItem().getOwner().getId().equals(userId) &&
                !booking.getBooker().getId().equals(userId)) {
            throw new BookingNotBookerAndItemNotOwnerException(
                    "Запись пренадлежит не этому пользователю или предмет не его");
        }
        return bookingMapper.toBookingDto(booking);
    }
}
