package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemController.class)
@Validated
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ItemService itemService;

    private final ItemDto itemDto = ItemDto.builder().id(1L).name("itemName")
            .description("itemDesc").available(true).build();

    @SneakyThrows
    @Test
    void addItem() {
        when(itemService.addItem(1L, itemDto)).thenReturn(itemDto);
        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(itemDto), result);

        verify(itemService).addItem(1L, itemDto);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);

        String result = mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(itemDto), result);

        verify(itemService).getItemById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        itemDto.setDescription("Updated Item Description");
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Updated Item Description", itemDto.getDescription());

        verify(itemService).updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class));
    }

    @SneakyThrows
    @Test
    void getAllItemsByOwnerId() {
        when(itemService.getAllItemsByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getAllItemsByOwnerId(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void searchItems() {
        ItemDto dtoSearch = ItemDto.builder().id(2L).name("Сумка спортивная")
                .description("Для тренировок").available(true)
                .build();
        when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(dtoSearch));

        String result = mockMvc.perform(get("/items/search")
                        .param("text", "сумка")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoSearch)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertFalse(result.isEmpty());

        verify(itemService).searchItems(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void searchItemWithIncorrectParameter() throws Exception {
        ItemDto dtoForSearch = ItemDto.builder().id(2L).name("Щетка для обуви")
                .description("Хорошо чистит").available(true)
                .build();
        when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(dtoForSearch));

        mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService, never()).searchItems(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void searchItemWithIncorrectPageParameters() {
        int invalidPage = -1;
        int invalidSize = -10;

        ItemDto dtoForSearch = ItemDto.builder().id(2L).name("Мыльница")
                .description("Красивая и удобная").available(true)
                .build();
        when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(dtoForSearch));

        mockMvc.perform(get("/items/search")
                        .param("text", "мыл")
                        .param("page", String.valueOf(invalidPage))
                        .param("size", String.valueOf(invalidSize)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItems(anyLong(), anyString(), eq(invalidPage), eq(invalidSize));
    }

    @SneakyThrows
    @Test
    void createItemComment() {
        CommentShortDto shortDto = CommentShortDto.builder().id(1L).text("test comment").itemId(1L)
                .authorName("authorName").created(LocalDateTime.now()).build();
        CommentDto commentDto = CommentDto.builder().id(1L).text("test comment").item(itemDto)
                .authorName("authorName").created(shortDto.getCreated()).build();

        when(itemService.addNewComment(any(CommentShortDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CommentDto actualDto = objectMapper.readValue(result, CommentDto.class);
        assertEquals(commentDto.getId(), actualDto.getId());
        assertEquals(commentDto.getText(), actualDto.getText());
        assertEquals(commentDto.getItem().getId(), actualDto.getItem().getId());
        assertEquals(commentDto.getAuthorName(), actualDto.getAuthorName());
        assertEquals(commentDto.getCreated(), actualDto.getCreated());

        verify(itemService).addNewComment(any(CommentShortDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void createItemCommentWithoutText() {
        CommentDto commentDto = CommentDto.builder().id(1L).text("test comment").item(itemDto)
                .authorName("authorName").created(LocalDateTime.now()).build();
        CommentShortDto shortInvalidDto = CommentShortDto.builder().text("").build();

        when(itemService.addNewComment(any(CommentShortDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(shortInvalidDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewComment(any(CommentShortDto.class), anyLong(), anyLong());
    }
}