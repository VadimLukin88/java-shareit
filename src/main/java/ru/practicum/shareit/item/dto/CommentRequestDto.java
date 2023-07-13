package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentRequestDto {
    @Null(groups = OnCreate.class, message = "Id для нового комментария назначается автоматически.")
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "Текст комментария не может быть пустым (null/blank).")
    private String text;
    private Long itemId;
    private Long authorId;
    @Null(groups = OnCreate.class, message = "Время создания назначается автоматически.")
    private LocalDateTime created;
}
