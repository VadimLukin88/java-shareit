package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestor; // id пользователя, создавшего запрос
    private LocalDateTime created;  // дата и время создания запроса
}
