package ru.practicum.shareit.bookingtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Test
    void addBookingTest() throws Exception {
        Long userId = 1L;

        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder().itemId(1L)
                .start(LocalDateTime.of(1, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2, 2, 2, 2, 2, 2))
                .build();

        BookingDto bookingDto = new BookingDto();

        when(bookingService.add(any(BookingDtoCreate.class), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDtoCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).add(bookingDtoCreate, userId);
    }

    @Test
    void updateStatusTest() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        BookingDto bookingDto = new BookingDto();

        when(bookingService.updateStatus(bookingId, approved, userId)).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).updateStatus(bookingId, approved, userId);
    }

    @Test
    void getByIdTest() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;

        BookingDto bookingDto = new BookingDto();

        when(bookingService.getById(userId, bookingId)).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getById(userId, bookingId);
    }

    @Test
    void getAllByBookerItemsTest() throws Exception {
        Long userId = 1L;
        Integer from = 1;
        Integer size = 20;
        String state = "All";

        List<BookingDto> bookingDtoList = List.of();

        when(bookingService.getAllByOwnerId(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllByOwnerId(state, userId, from, size);
    }

    @Test
    void getAllByBookerIdTest() throws Exception {
        Long userId = 1L;
        Integer from = 1;
        Integer size = 20;
        String state = "All";

        List<BookingDto> bookingDtoList = List.of();

        when(bookingService.getAllByBookerId(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(bookingDtoList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllByBookerId(state, userId, from, size);
    }
}
