package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemReqRespDto {
    private Long id;
    private String description;
//    private Long requestor; // id пользователя, создавшего запрос
    private LocalDateTime created;  // дата и время создания запроса
    private List<ItemRespDto> items;
}