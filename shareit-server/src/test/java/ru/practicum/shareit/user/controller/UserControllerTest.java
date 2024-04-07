package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.exceptoins.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private final UserDto userDto = UserDto.builder().id(1L).name("testUser").email("user@email.ru").build();

    @SneakyThrows
    @Test
    void testAddUser() {
        when(userService.addUser(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertTrue(result.contains("testUser"));
        assertTrue(result.contains("user@email.ru"));
        assertEquals(objectMapper.writeValueAsString(userDto), result);

        verify(userService).addUser(userDto);
    }

    @SneakyThrows
    @Test
    void testGetUsers() {
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("User2")
                .email("user2@email.com").build();
        List<UserDto> usersDto = List.of(userDto, userDto2);
        when(userService.getUsers()).thenReturn(usersDto);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(usersDto), result);

        verify(userService).getUsers();
    }

    @SneakyThrows
    @Test
    void testUpdateUser() {
        Long userId = userDto.getId();
        UserUpdateDto newUpdateUserDto = UserUpdateDto.builder()
                .name("newUserName")
                .email("newEmail@example.com")
                .build();
        UserDto newUserDto = UserDto.builder()
                .name("newUserName")
                .email("newEmail@example.com")
                .build();

        when(userService.updateUser(userId, newUpdateUserDto)).thenReturn(newUserDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUpdateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertTrue(result.contains("newUserName"));
        assertTrue(result.contains("newEmail@example.com"));

        verify(userService).updateUser(userId, newUpdateUserDto);
    }

    @SneakyThrows
    @Test
    void createNewUserWithInvalidNameOrEmail() {
        UserDto userWithoutName = UserDto.builder().id(1L).name(null).email("user@name.ru").build();
        UserDto userWithBlankEmail = UserDto.builder().id(1L).name("userName").email("").build();

        when(userService.addUser(any(UserDto.class))).thenAnswer(invocation -> {
            UserDto user = invocation.getArgument(0);
            if (user.getName() == null) {
                throw new BadRequestException("User name cannot be null");
            } else if (user.getEmail().isEmpty()) {
                throw new BadRequestException("User email cannot be blank");
            }
            return user;
        });

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userWithoutName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userWithBlankEmail))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void testGetUserId() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(userDto);
        MvcResult result = mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        assertNotNull(content);
        assertTrue(content.contains("testUser"));
        assertTrue(content.contains("user@email.ru"));
    }

    @SneakyThrows
    @Test
    void testDeleteUser() {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}