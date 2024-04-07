package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;

    private final User owner = User.builder().name("UserName").email("user@mail.ru").build();
    private final User commentator = User.builder().name("UserCommentator").email("commentator@mail.ru").build();
    private final Item item = Item.builder()
            .name("Книга")
            .description("Война и Мир")
            .available(true)
            .owner(owner)
            .build();
    private final Comment comment = Comment.builder()
            .text("Отличная книга")
            .created(LocalDateTime.now())
            .author(commentator)
            .item(item)
            .build();

    @BeforeEach
    void setUp() {
        userRepository.save(owner);
        userRepository.save(commentator);
        itemRepository.save(item);
        commentRepository.save(comment);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findByItemOrderByIdAsc() {
        List<Comment> actualsComment = commentRepository.findByItemOrderByIdAsc(item);

        assertFalse(actualsComment.isEmpty());
        assertEquals(1, actualsComment.size());

        Comment actualComment = actualsComment.get(0);
        assertEquals("Отличная книга",actualComment.getText());
        assertNotNull(actualComment.getCreated());
    }
}