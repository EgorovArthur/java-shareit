package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id; // уникальный идентификатор вещи
    @NotNull
    @NotEmpty
    @NotBlank
    private String name; // краткое название
    @NotNull
    @NotEmpty
    @NotBlank
    private String description; // развернутое описание
    @NotNull
    private Boolean available; // статус о доступности вещи для аренды
    private Integer ownerId; // владелец вещи
    private Integer requestId; // если вещь была создана по запросу др.польз, то в этом поле будет хран.ссылка на запрос
}

