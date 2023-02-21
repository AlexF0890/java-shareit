package ru.practicum.shareit.itemtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemMapper itemMapper;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void addTest() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("update@email.ru")
                .name("updateName")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        RequestItemDto itemDto = new RequestItemDto(1L, "name",
                "description", true, null);

        when(itemMapper.toItem(itemDto)).thenReturn(new Item());
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);

        when(itemService.add(itemDto, userId)).thenReturn(itemDto);
        RequestItemDto itemDto2 = itemService.add(itemDto, user.getId());

        assertEquals(itemDto2.getId(), itemDto.getId());
    }

    @Test
    void updateTest() {
        Long userId = 1L;
        Long id = 1L;
        User user = User.builder()
                .id(userId)
                .email("update@email.ru")
                .name("updateName")
                .build();
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Item item = new Item(id, "item", "description", true, user, null);
        RequestItemDto itemDto = new RequestItemDto(id, "nameUp",
                "descriptionUp", false, null);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        RequestItemDto itemDto1 = itemMapper.toRequestItemDto(item);
        RequestItemDto itemDto2 = itemService.update(itemDto, id, userId);
        assertEquals(itemDto2, itemDto1);
    }

    @Test
    void addTestNotFound() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("update@email.ru")
                .name("updateName")
                .build();

        RequestItemDto itemDto = new RequestItemDto(1L, "name",
                "description", true, null);

        when(itemMapper.toItem(itemDto)).thenReturn(new Item());
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);

        assertThrows(UserNotFoundException.class, () -> itemService.add(itemDto, userId));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void updateNotFoundItemTest() {
        Long userId = 1L;
        Long id = 1L;
        User user = User.builder()
                .id(userId)
                .email("update@email.ru")
                .name("updateName")
                .build();
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Item item = new Item(id, "item", "description", true, user, null);
        RequestItemDto itemDto = new RequestItemDto(id, "nameUp",
                "descriptionUp", false, null);

        assertThrows(ItemNotFoundException.class, () -> itemService.update(itemDto, id, userId));

        verify(itemRepository, never()).save(item);
    }

    @Test
    void getListSearchTest() {
        Long userId = 2L;
        User user = User.builder()
                .id(userId)
                .email("update2@email.ru")
                .name("update2Name")
                .build();
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String search = "name";
        int from = 0;
        int size = 4;

        List<Item> items = List.of(new Item());
        Page<Item> pages = new PageImpl<>(items);

        when(itemRepository.findPageByIsAvailableIsTrue(search, PageRequest.of(from, size))).thenReturn(pages);

        List<ItemDto> searchList = itemService.search(search, userId, from, size);
        List<ItemDto> itemDtoList = itemMapper.toListItemDto(items);

        assertEquals(searchList, itemDtoList);
    }
}
