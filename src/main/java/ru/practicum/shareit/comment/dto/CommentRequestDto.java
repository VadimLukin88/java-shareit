package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentRequestDto {
    @Null(groups = OnCreate.class, message = "Id для нового комментария назначается автоматически.")
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "Текст комментария не может быть пустым (null/blank).")
    private String text;
    @NotNull(groups = OnCreate.class, message = "Комментарий должен содержать Id вещи.")
    private Long itemId;
    @NotNull(groups = OnCreate.class, message = "Комментарий должен содержать Id пользователя.")
    private Long authorId;
    @Null(groups = OnCreate.class, message = "Время создания назначается автоматически.")
    private LocalDateTime created;
}
