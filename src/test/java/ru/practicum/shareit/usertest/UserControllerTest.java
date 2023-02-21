package ru.practicum.shareit.usertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Test
    void addUser() throws Exception {
        UserDto userDto = UserDto.builder().name("name").email("email@mail.ru").build();

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userService).add(userDto);
    }

    @Test
    void getAllUsers() throws Exception {
        UserDto userDto = UserDto.builder().id(1L).name("name").email("email@mail.ru").build();
        UserDto userDto1 = UserDto.builder().id(2L).name("name").email("email@mail.ru").build();
        List<UserDto> users = List.of(userDto, userDto1);

        when(userService.getAll()).thenReturn(users);

        String body = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(body, mapper.writeValueAsString(users));
    }

    @Test
    void upgradeUser() throws Exception {
        Long id = 1L;
        UserDto userDto = UserDto.builder().id(id).name("name").email("email@mail.ru").build();

        when(userService.update(any(UserDto.class), anyLong())).thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", id)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).update(userDto, id);
    }

    @Test
    void getByIdUser() throws Exception {
        Long id = 1L;
        UserDto userDto = UserDto.builder().id(id).name("name").email("email@mail.ru").build();

        when(userService.getId(id)).thenReturn(userDto);

        String body = mvc.perform(get("/users/{userId}", id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(body, mapper.writeValueAsString(userDto));
    }
}
