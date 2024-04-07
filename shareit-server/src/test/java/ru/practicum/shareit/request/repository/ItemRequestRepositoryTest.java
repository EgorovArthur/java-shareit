package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private final User owner = User.builder().name("UserName").email("user@mail.ru").build();
    private final User requestor = User.builder().name("UserRequestor").email("requestor@mail.ru").build();
    private final ItemRequest request = ItemRequest.builder()
            .description("Куплю ручку")
            .requestor(requestor)
            .created(LocalDateTime.now())
            .build();

    @BeforeEach
    void setUp() {
        userRepository.save(owner);
        userRepository.save(requestor);
        requestRepository.save(request);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }


    @Test
    void findAllByRequestorOrderByCreated() {
        List<ItemRequest> actualRequests = requestRepository.findAllByRequestorOrderByCreated(requestor);

        assertFalse(actualRequests.isEmpty());
        assertEquals(1, actualRequests.size());

        ItemRequest actualRequest = actualRequests.get(0);
        assertEquals("Куплю ручку", actualRequest.getDescription());
        assertEquals(requestor, actualRequest.getRequestor());
        assertNotNull(actualRequest.getCreated());
    }
}