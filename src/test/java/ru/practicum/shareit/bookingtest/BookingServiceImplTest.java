package ru.practicum.shareit.bookingtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void addTest() {
        Long userId = 1L;
        Long ownerId = 2L;
        Long itemId = 1L;
        BookingDtoCreate bookingCreationDto = BookingDtoCreate.builder().start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(20)).itemId(itemId).build();

        User owner = User.builder().id(ownerId).build();
        Item item = Item.builder().owner(owner).available(true).build();
        User user = User.builder().build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = Booking.builder().build();
        when(bookingMapper.toBooking(bookingCreationDto, user, item)).thenReturn(booking);

        BookingDto bookingDto = BookingDto.builder().build();
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto bookingDto1 = bookingService.add(bookingCreationDto, userId);

        assertEquals(bookingDto1, bookingDto);

        verify(bookingRepository).save(booking);
    }

    @Test
    void addNotUserFoundTest() {
        Long userId = 2L;
        Long ownerId = 1L;
        Long itemId = 1L;
        BookingDtoCreate bookingCreationDto = BookingDtoCreate.builder().start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(20)).itemId(itemId).build();

        User owner = User.builder().id(ownerId).build();
        Item item = Item.builder().owner(owner).available(true).build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        assertThrows(UserNotFoundException.class, () -> bookingService.add(bookingCreationDto, userId));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addNotItemFoundTest() {
        Long userId = 2L;
        Long itemId = 1L;
        BookingDtoCreate bookingCreationDto = BookingDtoCreate.builder().start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(20)).itemId(itemId).build();

        assertThrows(ItemNotFoundException.class, () -> bookingService.add(bookingCreationDto, userId));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addItemNotAvailableTest() {
        Long userId = 2L;
        Long ownerId = 1L;
        Long itemId = 1L;
        BookingDtoCreate bookingCreationDto = BookingDtoCreate.builder().start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(20)).itemId(itemId).build();

        User owner = User.builder().id(ownerId).build();
        User user = User.builder().id(userId).build();
        Item item = Item.builder().owner(owner).available(false).build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(ItemNotAvailableException.class, () -> bookingService.add(bookingCreationDto, userId));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addBookingStartTimeAfterEndTimeTest() {
        Long userId = 2L;
        Long ownerId = 1L;
        Long itemId = 1L;
        BookingDtoCreate bookingCreationDto = BookingDtoCreate.builder().start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(1)).itemId(itemId).build();

        User owner = User.builder().id(ownerId).build();
        User user = User.builder().id(userId).build();
        Item item = Item.builder().owner(owner).available(true).build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(BookingStartTimeAfterEndTimeException.class,
                () -> bookingService.add(bookingCreationDto, userId));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateStatusTest() {
        boolean status = false;
        Long ownerId = 2L;
        Long itemId = 1L;
        Long bookingId = 1L;

        User owner = User.builder().id(ownerId).build();
        Item item = Item.builder().id(itemId).owner(owner).available(true).build();
        User user = User.builder().build();
        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        Booking booking = Booking.builder().item(item).status(STATUS.WAITING).build();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ItemDto itemDto = ItemDto.builder().id(itemId).build();
        BookingDto bookingDto = BookingDto.builder().status(STATUS.REJECTED).item(itemDto).build();
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto bookingDto1 = bookingService.updateStatus(bookingId, status, ownerId);

        assertEquals(bookingDto1, bookingDto);
    }

    @Test
    void updateStatusApprovedTest() {
        boolean status = false;
        Long ownerId = 2L;
        Long itemId = 1L;
        Long bookingId = 1L;

        User owner = User.builder().id(ownerId).build();
        Item item = Item.builder().id(itemId).owner(owner).available(true).build();
        User user = User.builder().build();
        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        Booking booking = Booking.builder().item(item).status(STATUS.APPROVED).build();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ItemDto itemDto = ItemDto.builder().id(itemId).build();
        BookingDto bookingDto = BookingDto.builder().status(STATUS.REJECTED).item(itemDto).build();
        lenient().when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        assertThrows(BookingChangeStatusApprovedException.class,
                () -> bookingService.updateStatus(bookingId, status, ownerId));
    }

    @Test
    void getByIdTest() {
        Long userId = 1L;
        Long ownerId = 2L;
        Long itemId = 1L;
        Long bookingId = 1L;

        User owner = User.builder().id(ownerId).build();
        Item item = Item.builder().id(itemId).owner(owner).available(true).build();
        User user = User.builder().id(userId).build();
        lenient().when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        Booking booking = Booking.builder().booker(user).item(item).status(STATUS.WAITING).build();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ItemDto itemDto = ItemDto.builder().id(itemId).build();
        UserDto userDto = UserDto.builder().id(userId).build();
        BookingDto bookingDto = BookingDto.builder().booker(userDto).status(STATUS.REJECTED).item(itemDto).build();
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto bookingDto1 = bookingService.getById(userId, bookingId);

        assertEquals(bookingDto1, bookingDto);
    }

    @Test
    void getAllByBookerId() {
        Long userId = 1L;
        String string = "ALL";

        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByBookerIdOrderByBookerId(userId))
                .thenReturn(List.of());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByBookerId(string, userId, null, null);

        assertEquals(bookings, bookings1);

        verify(bookingRepository).findByBookerIdOrderByBookerId(userId);
    }

    @Test
    void getAllByPageBookerId() {
        Long userId = 1L;
        String string = "ALL";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByBookerIdOrderByBookerId(
                        PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByBookerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository).findByBookerIdOrderByBookerId(PageRequest.of(from, size));
    }

    @Test
    void getAllByPagePastBookerId() {
        Long userId = 1L;
        String string = "PAST";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByBookerIdAndEndDateIsBefore(userId,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByBookerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository).findByBookerIdAndEndDateIsBefore(userId, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageFutureBookerId() {
        Long userId = 1L;
        String string = "FUTURE";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByBookerIdAndStartDateIsAfter(userId,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByBookerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository).findByBookerIdAndStartDateIsAfter(userId, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageWaitingBookerId() {
        Long userId = 1L;
        String string = "WAITING";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.WAITING,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByBookerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository)
                .findByBookerIdAndStatusEquals(userId, STATUS.WAITING, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageCurrentBookerId() {
        Long userId = 1L;
        String string = "CURRENT";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByBookerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository)
                .findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageRejectedBookerId() {
        Long userId = 1L;
        String string = "REJECTED";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByBookerIdAndStatusEquals(userId, STATUS.REJECTED,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByBookerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository).findByBookerIdAndStatusEquals(userId, STATUS.REJECTED, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageOwnerId() {
        Long userId = 1L;
        String string = "ALL";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByItemOwnerId(userId,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByOwnerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository).findByItemOwnerId(userId, PageRequest.of(from, size));
    }

    @Test
    void getAllByPagePastOwnerId() {
        Long userId = 1L;
        String string = "PAST";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByItemOwnerIdAndEndDateIsBefore(userId,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByOwnerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository).findByItemOwnerIdAndEndDateIsBefore(userId, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageFutureOwnerId() {
        Long userId = 1L;
        String string = "FUTURE";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByItemOwnerIdAndStartDateIsAfter(userId,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByOwnerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository).findByItemOwnerIdAndStartDateIsAfter(userId, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageWaitingOwnerId() {
        Long userId = 1L;
        String string = "WAITING";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByItemOwnerIdAndStatusEquals(userId, STATUS.WAITING,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByOwnerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository)
                .findByItemOwnerIdAndStatusEquals(userId, STATUS.WAITING, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageCurrentOwnerId() {
        Long userId = 1L;
        String string = "CURRENT";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(userId,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByOwnerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository)
                .findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(userId, PageRequest.of(from, size));
    }

    @Test
    void getAllByPageRejectedOwnerId() {
        Long userId = 1L;
        String string = "REJECTED";
        int from = 0;
        int size = 20;

        User user = User.builder().id(userId).build();
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        lenient().when(bookingRepository.findByItemOwnerIdAndStatusEquals(userId, STATUS.REJECTED,
                PageRequest.of(from, size))).thenReturn(Page.empty());

        List<BookingDto> bookings = Collections.emptyList();
        List<BookingDto> bookings1 = bookingService.getAllByOwnerId(string, userId, from, size);

        assertEquals(bookings, bookings1);

        verify(bookingRepository)
                .findByItemOwnerIdAndStatusEquals(userId, STATUS.REJECTED, PageRequest.of(from, size));
    }
}
