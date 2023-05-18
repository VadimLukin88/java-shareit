package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
            .map(userMapper::mapUserToDto)
            .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userMapper.mapUserToDto(userStorage.getUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.mapDtoToUser(userDto);
        return userMapper.mapUserToDto(userStorage.createUser(user));
    }

    @Override
    public UserDto modifyUser(Long userId, UserDto userDto) {
        User user = userMapper.mapDtoToUser(userDto);
        return userMapper.mapUserToDto(userStorage.modifyUser(userId, user));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
