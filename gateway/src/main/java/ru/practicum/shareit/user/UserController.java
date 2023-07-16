package ru.practicum.shareit.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.OnCreate;
import ru.practicum.shareit.user.dto.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("HTTP_GET: Получен запрос на получение всех пользователей.");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("HTTP_GET: Получен запрос на получение пользователя с Id = " + userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("HTTP_POST: Получен запрос на создание пользователя " + userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    @Validated(OnUpdate.class)
    public ResponseEntity<Object> modifyUser(@PathVariable Long userId,
                              @Valid @RequestBody UserDto userDto) {
        log.info("HTTP_PATCH: Получен запрос на изменение пользователя с Id = " + userId
                  + ". Обновляемые данные: " + userDto);
        return userClient.modifyUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("HTTP_DELETE: Получен запрос на удаление пользователя с Id = " + userId);
        return userClient.deleteUser(userId);
    }

}
