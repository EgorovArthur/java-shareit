package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptoins.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, Long userId) {
        User requestor = userRepository.findById(userId). orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDto);
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);
        itemRequestRepository.save(request);
        log.info("Пользователь с id={} добавил запрос с id={}", requestor.getId(), request.getId());
        return ItemRequestMapper.toItemRequestDto(request);
    }

    @Override
    public Collection<ItemRequestDto> getUserRequests(Long userId) {
        User requestor = userRepository.findById(userId). orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorOrderByCreated(requestor);
        fillItemsByRequests(requests);
        return ItemRequestMapper.toItemRequestDtoListItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId). orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id=%d не найден", requestId)));
        fillItemsByRequests(List.of(request));
        log.info("Запрос с id={} успешно получен пользователем id={}", requestId, userId);
        return ItemRequestMapper.toItemRequestDto(request);
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsForAllUsers(Long userId, Integer from, Integer size) {
        User requestor = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
        PageRequest page = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requests = itemRequestRepository.findAll(page).stream()
                .filter(request -> !request.getRequestor().equals(requestor))
                .collect(Collectors.toList());
        fillItemsByRequests(requests);
        log.info("Список всех запросов успешно получен пользователем с id={}", userId);
        return ItemRequestMapper.toItemRequestDtoListItems(requests);
    }

    private void fillItemsByRequests(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, Set<Item>> items = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(groupingBy(item -> item.getRequest().getId(), toSet()));

        requests.forEach(itemRequest -> itemRequest.setItems(items.getOrDefault(itemRequest.getId(),
                Collections.emptySet())));
    }
}
