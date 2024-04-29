package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final User owner = User.builder()
            .name("Username1")
            .email("user@mail.ru")
            .build();
    private final Item item = Item.builder()
            .name("Стол")
            .description("Письменный, черный")
            .available(true)
            .owner(owner)
            .build();

    @BeforeEach
    void add() {
        userRepository.save(owner);
        itemRepository.save(item);
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findByOwner() {
        List<Item> actualItems = itemRepository.findByOwner(owner, PageRequest.of(0, 10));

        assertFalse(actualItems.isEmpty());
        assertEquals(1, actualItems.size());
        assertEquals("Стол", actualItems.get(0).getName());
    }

    @Test
    void search() {
            List<Item> actualItems = itemRepository.search("письмен", PageRequest.of(0, 10));

            assertFalse(actualItems.isEmpty());
            assertEquals(1, actualItems.size());
        }

    @Test
    void findAllByRequestIdIn() {
        User requestor = User.builder().name("requestor").email("req@mail.ru").build();
        userRepository.save(requestor);

        ItemRequest request = ItemRequest.builder().description("Куплю ручку").requestor(requestor).build();
        request.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request);

        Item itemWithRequest = Item.builder().name("Ручка").description("Ручка гелеевая синяя").available(true)
                .owner(owner).request(request).build();
        itemRepository.save(itemWithRequest);

        List<Item> actualItems = itemRepository.findAllByRequestIdIn(List.of(request.getId()));

        assertFalse(actualItems.isEmpty());
        assertEquals(1, actualItems.size());

    }
}