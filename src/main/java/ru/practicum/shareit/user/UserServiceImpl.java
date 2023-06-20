package ru.practicum.shareit.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapUserToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                             .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));
        return UserMapper.mapUserToDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapDtoToUser(userDto);
        return UserMapper.mapUserToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto modifyUser(Long userId, UserDto userDto) {
        User savedUser = userRepository.findById(userId)
                                       .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));

        User user = UserMapper.mapDtoToUser(userDto);

        if (user.getEmail() != null) {
            savedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }
        return UserMapper.mapUserToDto(userRepository.save(savedUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsUserById(userId)) {
            throw new DataNotFoundException("Пользователь с Id " + userId + " не найден.");
        }
        userRepository.deleteById(userId);
    }

}
