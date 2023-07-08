package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemRespDto {
    private Long id;
    private String name;
    private Long ownerId;
    private String description;
    private boolean available;
    private long requestId;
}
