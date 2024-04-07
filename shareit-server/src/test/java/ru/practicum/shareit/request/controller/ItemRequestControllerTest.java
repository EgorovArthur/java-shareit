package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ItemRequestService requestService;
    private final ItemDto itemDto = ItemDto.builder().id(1L).name("itemName").description("itemDesc")
            .available(true).build();
    private final ItemRequestDto requestDto = ItemRequestDto.builder().id(1L).description("desc")
            .created(LocalDateTime.now()).requestorId(1L).items(Set.of(itemDto)).build();

    @SneakyThrows
    @Test
    void createNewRequest() {
        when(requestService.addRequest(requestDto, 1L)).thenReturn(requestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(result);
        assertTrue(result.contains("desc"));

        verify(requestService).addRequest(requestDto, 1L);
    }

    @SneakyThrows
    @Test
    void createNewRequest_whenDescriptionEmpty_shouldThrowBadRequestException() {
        ItemRequestDto requestDtoWithEmptyDescription = ItemRequestDto.builder()
                .id(1L)
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoWithEmptyDescription)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).addRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequests() {
        when(requestService.getUserRequests(1L)).thenReturn(List.of(requestDto));
        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(List.of(requestDto)), result);

        verify(requestService).getUserRequests(1L);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        when(requestService.getAllRequestsForAllUsers(1L, 0, 10)).thenReturn(List.of(requestDto));
        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(List.of(requestDto)), result);

        verify(requestService).getAllRequestsForAllUsers(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenFromNegative_shouldThrowBadRequestException() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getAllRequestsForAllUsers(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        when(requestService.getRequestById(1L, 1L)).thenReturn(requestDto);
        String result = mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(result);
        assertEquals(result, objectMapper.writeValueAsString(requestDto));

        verify(requestService).getRequestById(1L, 1L);
    }
}