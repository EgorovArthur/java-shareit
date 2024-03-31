package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptoins.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ItemRequestServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestRepository requestRepository;
    @InjectMocks
    ItemRequestServiceImpl requestService;
    private final User requestor = new User(2L, "UserName", "username@mail.ru");
    private final User owner = new User(1L, "ownerName", "owner@mail.ru");
    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .id(1L)
            .description("reqDesc")
            .requestorId(2L)
            .created(LocalDateTime.now())
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("reqDes")
            .requestor(requestor)
            .created(LocalDateTime.now())
            .build();

    @Test
    void addRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));

        ItemRequestDto result = requestService.addRequest(requestDto, requestor.getId());

        assertNotNull(result);
        assertEquals(requestDto.getDescription(), result.getDescription());
        assertEquals(requestor.getId(), result.getRequestorId());

        verify(userRepository).findById(anyLong());
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void addRequest_shouldThrowNotFoundException_whenUserNotFound() {

        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.addRequest(requestDto, 100L));

        verify(userRepository).findById(100L);
        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getUserRequests() {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);

        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestorOrderByCreated(requestor)).thenReturn(requests);

        Collection<ItemRequestDto> result = requestService.getUserRequests(requestor.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepository).findById(requestor.getId());
        verify(requestRepository).findAllByRequestorOrderByCreated(requestor);
    }

    @Test
    void getUserRequests_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.addRequest(requestDto, 100L));

        verify(userRepository).findById(100L);
        verify(requestRepository, never()).findAllByRequestorOrderByCreated(any(User.class));
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = requestService.getRequestById(requestor.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals(result.getId(), itemRequest.getId());
        assertEquals(result.getDescription(), itemRequest.getDescription());
        assertEquals(result.getCreated(), itemRequest.getCreated());
        assertNotNull(result.getItems());

        verify(userRepository).findById(requestor.getId());
        verify(requestRepository).findById(itemRequest.getId());
    }

    @Test
    void getRequestById_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(100L, any()));

        verify(userRepository).findById(100L);
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestById_shouldThrowNotFoundException_whenRequestNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.findById(100L)).thenThrow(new NotFoundException("Запрос не найден"));

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(2L, 100L));

        verify(userRepository).findById(anyLong());
        verify(requestRepository).findById(100L);
    }

    @Test
    void getAllRequestsForAllUsers() {
        ItemRequest secondRequest = ItemRequest.builder().id(2L).description("req2Desc").requestor(requestor)
                .created(LocalDateTime.now()).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(requestRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest, secondRequest)));

        Collection<ItemRequestDto> result = requestService
                .getAllRequestsForAllUsers(owner.getId(), 0, 10);

        assertNotNull(result);
        assertNotNull(requestDto.getId());
        assertNotNull(requestDto.getDescription());
        assertNotNull(requestDto.getRequestorId());
        assertNotNull(requestDto.getCreated());
        assertEquals(2, result.size());

        verify(userRepository).findById(owner.getId());
        verify(requestRepository).findAll(any(PageRequest.class));
    }

    @Test
    void getAllRequestsForAllUsers_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            requestService.getAllRequestsForAllUsers(100L, 0, 10);
        });

        verify(userRepository).findById(100L);
        verify(requestRepository, never()).findAll(any(PageRequest.class));
    }
}