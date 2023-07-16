package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto userDto);

    UserDto modifyUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

}
