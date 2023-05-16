package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    User modifyUser(Long userId, User user);

    void deleteUser(Long id);

    boolean isUserExist(Long id);
}
