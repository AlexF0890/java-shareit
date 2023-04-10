package ru.practicum.shareit.itemtest.itemrequesttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestCreateDto;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Test
    void addItemRequest() throws Exception {
        Long userId = 1L;
        ItemRequestCreateDto itemRequestDto = ItemRequestCreateDto.builder().description("description").build();

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();

        when(itemRequestService.add(any(ItemRequestCreateDto.class), anyLong())).thenReturn(itemRequestDto1);

        mvc.perform(post("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).add(itemRequestDto, userId);
    }

    @Test
    void getById() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestDto itemRequestDto = new ItemRequestDto(requestId, "string", LocalDateTime.now(), List.of());

        when(itemRequestService.getId(requestId, userId)).thenReturn(itemRequestDto);

        String body = mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(body, mapper.writeValueAsString(itemRequestDto));
    }

    @Test
    void getAllByRequestIdTest() throws Exception {
        Long userId = 1L;

        List<ItemRequestDto> itemRequestDto = List.of();

        when(itemRequestService.getAllByRequesterId(userId)).thenReturn(itemRequestDto);

        String body = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(body, mapper.writeValueAsString(itemRequestDto));
    }

    @Test
    void getPageAllByRequestIdTest() throws Exception {
        Long userId = 1L;

        List<ItemRequestDto> itemRequestDto = List.of();

        when(itemRequestService.getPageAllByRequestId(userId, 0, 20)).thenReturn(itemRequestDto);

        String body = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(body, mapper.writeValueAsString(itemRequestDto));
    }
}
