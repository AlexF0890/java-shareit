package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            log.error("Пользователя не существует");
            throw new UserNotFoundException("Пользователя не существует");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Предмет должен иметь статус");
            throw new ItemNotAvailableException("Предмет должен иметь статус");
        }
        if (itemDto.getName().isEmpty()) {
            log.error("Предмет должен иметь имя");
            throw new ItemNotNullNameException("Предмет должен иметь имя");
        }

        if (itemDto.getDescription() == null) {
            log.error("Предмет должен иметь описание");
            throw new ItemNotNullDescriptionException("Предмет должен иметь описание");
        }

        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(ownerId).orElseThrow());
        itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        } else {
            log.error("Данной вещи не существует");
            throw new ItemNotFoundException("Данной вещи не существует");
        }
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Данной вещи не существует"));

        if (!userRepository.existsById(userId)) {
            log.error("Пользователя не существует");
            throw new UserNotFoundException("Пользователя не существует");
        }

        if (!item.getOwner().getId().equals(userId)) {
            log.error("Пользователю не принадлежит данная вещь");
            throw new UserNotBelongsItemException("Пользователю не принадлежит данная вещь");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto getId(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Данной вещи не существует"));
        ItemDto itemDto = itemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findCommentsByItemId(item.getId());
        itemDto.setComments(commentMapper.toListCommentDto(comments));
        if (!userId.equals(item.getOwner().getId())) {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        } else {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(
                    itemId, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStartAsc(
                    itemId, LocalDateTime.now());
            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(lastBooking.map(BookingMapper::toItemBookingDto).orElse(null));
            }
            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(nextBooking.map(BookingMapper::toItemBookingDto).orElse(null));
            }
        }
        return itemDto;
    }

     @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        List<ItemDto> items = itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        List<Long> itemIds = items.stream().map(ItemDto::getId).collect(Collectors.toList());
        for (ItemDto itemDto: items) {
            List<Comment> comments = commentRepository.findAllByItemIdIn(itemIds)
                    .stream()
                    .filter(comment -> comment.getItem().getId().equals(itemDto.getId()))
                    .collect(Collectors.toList());
            itemDto.setComments(commentMapper.toListCommentDto(comments));

            List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);
            bookings.stream().filter(b -> b.getItem().getId().equals(itemDto.getId()))
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .ifPresent(lastBooking -> itemDto.setLastBooking(BookingMapper.toItemBookingDto(lastBooking)));

            bookings.stream().filter(b -> b.getItem().getId().equals(itemDto.getId()))
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .ifPresent(nextBooking -> itemDto.setNextBooking(BookingMapper.toItemBookingDto(nextBooking)));
        }
        return items;

    }

    @Override
    public List<ItemDto> search(String search, Long userId) {
        if (StringUtils.isEmpty(search)) {
            return new ArrayList<>();
        }
        if (!userRepository.existsById(userId)) {
            log.error("Пользователя не существует");
            throw new UserNotFoundException("Пользователя не существует");
        }

        List<Item> items = itemRepository.findByIsAvailableIsTrue(search.toLowerCase());
        return itemMapper.toListItemDto(items);
    }
}
