package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
//    @Null(groups = OnCreate.class, message = "При добавлении вещи Id назначается автоматически.")
    private Long id;
//    @NotBlank(groups = OnCreate.class, message = "У вещи должно быть имя.")
    private String name;
//    @NotBlank(groups = OnCreate.class, message = "У вещи должно быть описание.")
   private String description;
//    @NotNull(groups = OnCreate.class, message = "У вещи должен быть статус бронирования.")
    private Boolean available;
    private Long owner;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Long requestId;
    private List<CommentShortDto> comments;
}