package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.OnCreate;
import ru.practicum.shareit.user.dto.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private UserService userService;

    @Autowired
    public UserController (UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById (@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public UserDto createUser (@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    @Validated(OnUpdate.class)
    public UserDto modifyUser (@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        return userService.modifyUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser (@PathVariable Long userId) {
        userService.deleteUser(userId);
        return String.format("Пользователь с Id = %s удалён.", userId);
    }
}
