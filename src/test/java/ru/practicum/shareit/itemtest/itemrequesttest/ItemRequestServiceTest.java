package ru.practicum.shareit.itemtest.itemrequesttest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.RequestItemDto;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemRequestMapper itemRequestMapper;

    @Mock
    ItemMapper itemMapper;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void addRequest() {
        User user = User.builder().id(1L).name("one").email("one@mail.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder().description("description").build();
        ItemRequest itemRequest = ItemRequest.builder().description("description")
                .created(LocalDateTime.now()).build();
        when(itemRequestMapper.toItemRequestCreateDto(itemRequestCreateDto, user)).thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();

        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto itemRequestDto1 = itemRequestService.add(itemRequestCreateDto, user.getId());
        assertEquals(itemRequestDto1, itemRequestDto);

        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void getExistByIdTestUserNotFound() {
        User user = User.builder().id(1L).name("one").email("one@mail.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Long id = 1L;

        lenient().when(itemRequestRepository.findById(id)).thenReturn(Optional.empty());

        ItemRequestNotFoundException exception =
                assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getId(id, user.getId()));

        assertEquals(exception.getMessage(), "Записи не существует");
    }

    @Test
    void getExistByIdTestItemRequestNotFound() {
        Long id = 1L;
        Long userId = 1L;

        lenient().when(itemRequestRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> itemRequestService.getId(id, userId));

        assertEquals(exception.getMessage(), "Пользователя не существует");
    }

    @Test
    void getByIdItemRequestTest() {
        Long userId = 1L;
        Long itemRequestId = 1L;
        User user = User.builder().id(userId).name("one").email("one@mail.ru").build();
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequest itemRequest = ItemRequest.builder().id(itemRequestId).description("test")
                .created(LocalDateTime.now()).requester(user).build();
        List<Item> items = List.of();
        List<RequestItemDto> requestItemDto = List.of();
        lenient().when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(items);
        lenient().when(itemMapper.toListRequestItemDto(items)).thenReturn(requestItemDto);
        lenient().when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();

        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);
        ItemRequestDto itemRequestDto1 = itemRequestMapper.toItemRequestDto(itemRequest);
        ItemRequestDto itemRequestDto2 = itemRequestService.getId(itemRequestId, userId);

        assertEquals(itemRequestDto2, itemRequestDto1);
    }

    @Test
    void getAllItemRequest() {
        Long userId = 1L;
        User user = User.builder().id(userId).name("one").email("one@mail.ru").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("test")
                .created(LocalDateTime.now()).requester(user).build();
        List<Item> items = List.of();
        List<RequestItemDto> requestItemDto = List.of();
        lenient().when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(items);
        lenient().when(itemMapper.toListRequestItemDto(items)).thenReturn(requestItemDto);
        lenient().when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.findAllByRequesterIdIsNot(userId, PageRequest.of(0, 20))).thenReturn(Page.empty(PageRequest.of(0, 20)));

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getPageAllByRequestId(userId, 0, 20);

        verify(itemRequestRepository).findAllByRequesterIdIsNot(userId, PageRequest.of(0, 20));
    }

    @Test
    void getAllByRequestId() {
        Long userId = 1L;
        User user = User.builder().id(userId).name("one").email("one@mail.ru").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("test")
                .created(LocalDateTime.now()).requester(user).build();
        List<Item> items = List.of();
        List<RequestItemDto> requestItemDto = List.of();
        lenient().when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(items);
        lenient().when(itemMapper.toListRequestItemDto(items)).thenReturn(requestItemDto);
        lenient().when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        lenient().when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(new ItemRequestDto());
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)).thenReturn(List.of(itemRequest));
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllByRequesterId(userId);
        assertEquals(itemRequestDtoList, List.of(itemRequestDto));

        verify(itemRequestRepository).findAllByRequesterIdOrderByCreatedDesc(userId);
    }
}
