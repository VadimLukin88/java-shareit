package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long userId);

    User createUser(UserDto userDto);

    User modifyUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}
