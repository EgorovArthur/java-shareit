package ru.practicum.shareit_gatevay.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit_gatevay.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ItemRequestDto {

    private Long id; //уникальный идентификатор запроса;
    private String description; //текст запроса, содержащий описание требуемой вещи;
    private Long requestorId; //пользователь, создавший запрос;
    private LocalDateTime created; //дата и время создания запроса.
    private Set<ItemDto> items;
}
