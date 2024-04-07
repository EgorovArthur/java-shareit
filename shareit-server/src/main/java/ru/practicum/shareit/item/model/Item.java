package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор вещи
    @Column(name = "name")
    private String name; // краткое название
    @Column(name = "description")
    private String description; // развернутое описание
    @Column(name = "is_available")
    private Boolean available; // статус о доступности вещи для аренды
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; //владелец вещи
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request; // если вещь была создана по запросу др.польз, то в этом поле будет хран.ссылка на запрос
}
