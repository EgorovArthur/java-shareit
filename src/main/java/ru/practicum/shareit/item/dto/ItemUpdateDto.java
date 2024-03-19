package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemUpdateDto {

    private String name; //краткое наименование вещи;
    private String description; //развёрнутое описание;
    private Boolean available; //статус о том, доступна или нет вещь для аренды;
    private Integer requestId; //если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос.
}
