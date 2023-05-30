package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.OnCreate;
import ru.practicum.shareit.user.dto.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "У вещи должно быть имя.")
    @NotBlank(groups = OnUpdate.class, message = "Имя вещи не может быть пустым")
    private String name;
    @NotBlank(groups = OnCreate.class, message = "У вещи должно быть описание.")
    @NotBlank(groups = OnUpdate.class, message = "Описание вещи не может быть пустым")
    private String description;
    @NotNull(groups = OnCreate.class, message = "У вещи должен быть статус бронирования.")
    @NotNull(groups = OnUpdate.class, message = "У вещи должен быть статус бронирования (true/false).")
    private Boolean available;
    private Long owner;
//    private ItemRequest request;
}
