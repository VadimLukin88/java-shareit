package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
//    @Null(groups = OnCreate.class, message = "При добавлении вещи Id назначается автоматически.")
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "У вещи должно быть имя.")
    private String name;
    @NotBlank(groups = OnCreate.class, message = "У вещи должно быть описание.")
   private String description;
    @NotNull(groups = OnCreate.class, message = "У вещи должен быть статус бронирования.")
    private Boolean available;
    private Long owner;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Long requestId;
    private List<CommentShortDto> comments;
}
