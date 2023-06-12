package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
//    @Null(message = "Id для объекта назначается автоматически.")
//    private Long id;
    @NotNull(message = "Не задано время начала бронирования.")
    @FutureOrPresent(message = "Время начала бронирования должно быть позже текущего времени.")
    private LocalDateTime start;
    @NotNull(message = "Не задано время завершения бронирования.")
    @Future(message = "Время завершения бронирования не может быть ранее текущего времени.")
    private LocalDateTime end;
    @NotNull(message = "Не задан идентификатор вещи, для которого создаётся бронирование.")
    private Long itemId;
//    private User booker;
//    private BookingStatus status;
}
