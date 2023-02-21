package ru.practicum.shareit.itemtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.*;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;

    @MockBean
    CommentService commentService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void addItem() throws Exception {
        Long userId = 1L;
        RequestItemDto requestItemDto = RequestItemDto.builder().name("name")
                .description("description").available(true).build();

        when(itemService.add(any(RequestItemDto.class), anyLong())).thenReturn(requestItemDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(requestItemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).add(requestItemDto, userId);
    }

    @Test
    void updateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        RequestItemDto requestItemDto = RequestItemDto.builder().id(itemId).name("name")
                .description("description").available(true).build();

        when(itemService.update(any(RequestItemDto.class), anyLong(), anyLong())).thenReturn(requestItemDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(requestItemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).update(requestItemDto, userId, itemId);
    }

    @Test
    void getItemTest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder().id(itemId).name("name")
                .description("description").available(true).build();

        when(itemService.getId(itemId, userId)).thenReturn(itemDto);

        String body = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(body, mapper.writeValueAsString(itemDto));
    }

    @Test
    void getAllItemParamNullTest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService).getItemsByUserId(userId, null, null);
    }

    @Test
    void getAllSearchItemTest() throws Exception {
        Long userId = 0L;
        String search = "text";

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", search))
                .andExpect(status().isOk());

        verify(itemService).search(search, userId, null, null);
    }

    @Test
    void addCommentTest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDtoCreation commentDtoCreation = new CommentDtoCreation("string");

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .content(mapper.writeValueAsString(commentDtoCreation)))
                .andExpect(status().isOk());

        verify(commentService).addComment(itemId, userId, commentDtoCreation);
    }
}
