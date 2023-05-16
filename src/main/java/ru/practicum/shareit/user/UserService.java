package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createUser(UserDto userDto);

    UserDto modifyUser(Long userId, UserDto userDto);

    void deleteUser(Long id);
}
