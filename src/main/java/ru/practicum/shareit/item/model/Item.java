package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item {
    private Long id; // уникальный идентификатор вещи
    private String name; // краткое название
    private String description; // развернутое описание
    private Boolean available; // статус о доступности вещи для аренды
    private User owner; //владелец вещи
    private ItemRequest request; // если вещь была создана по запросу др.польз, то в этом поле будет хран.ссылка на запрос
}
