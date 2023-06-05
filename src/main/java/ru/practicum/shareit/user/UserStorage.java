package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(Long userId);

    User createUser(User user);

    User modifyUser(Long userId, User user);

    void deleteUser(Long userId);

    void isUserExist(Long userId);
}
