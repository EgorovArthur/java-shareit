package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //уникальный идентификатор запроса;
    @Column(name = "description")
    private String description; //текст запроса, содержащий описание требуемой вещи;
    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor; //пользователь, создавший запрос;
    @Column(name = "created_at")
    private LocalDateTime created; //дата и время создания запроса.
}
