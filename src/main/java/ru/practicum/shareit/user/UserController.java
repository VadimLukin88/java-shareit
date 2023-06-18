package ru.practicum.shareit.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.OnCreate;
import ru.practicum.shareit.user.dto.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("HTTP_GET: Получен запрос на получение всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("HTTP_GET: Получен запрос на получение пользователя с Id = " + userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("HTTP_POST: Получен запрос на создание пользователя " + userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    @Validated(OnUpdate.class)
    public UserDto modifyUser(@PathVariable Long userId,
                              @Valid @RequestBody UserDto userDto) {
        log.info("HTTP_PATCH: Получен запрос на изменение пользователя с Id = " + userId
                  + ". Обновляемые данные: " + userDto);
        return userService.modifyUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public Map<String, String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("HTTP_DELETE: Получен запрос на удаление пользователя с Id = " + userId);
        return Map.of("Result", String.format("Пользователь с Id = %s удалён.", userId));
    }

}
