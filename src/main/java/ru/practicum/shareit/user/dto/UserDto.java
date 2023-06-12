package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
public class UserDto {
    @Null(groups = OnCreate.class, message = "При создании пользователя поле Id должно быть пустым/null.")
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "Имя пользователя не может быть пустым")
    private final String name;
    @NotBlank(groups = OnCreate.class, message = "Email пользователя не может быть пустым")
    @Email(groups = OnCreate.class, message = "Некорректный e-mail пользователя")
    @Email(groups = OnUpdate.class, message = "Некорректный e-mail пользователя")
    private final String email;
}
