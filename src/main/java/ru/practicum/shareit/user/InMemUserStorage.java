package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EntryAlreadyExists;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemUserStorage implements UserStorage {
    private Long id = 0L;
    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User getUserById(Long userId) {
        isUserExist(userId);
        return userStorage.get(userId);
    }

    @Override
    public User createUser(User user) {
        isEmailExist(user);
        user.setId(++id);
        userStorage.put(id, user);
        return user;
    }

    @Override
    public User modifyUser(Long userId, User user) {
        isUserExist(userId);
        user.setId(userId);
        User savedUser = userStorage.get(userId);

        if (user.getEmail() != null) {
            isEmailExist(user);
            savedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }
        return userStorage.put(userId, savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        isUserExist(userId);
        userStorage.remove(id);
    }

    @Override
    public void isUserExist(Long userId) {
        if (!userStorage.containsKey(userId)) {
            throw new DataNotFoundException(String.format("Пользователь с Id = %s не найден", userId));
        }
    }

    private void isEmailExist(User user) {
        Optional<User> existUser = Optional.empty();

        if (user.getId() != null) {
            existUser = userStorage.values()
                                  .stream()
                                  .filter(u -> user.getEmail().equals(u.getEmail()))
                                  .filter(u -> !user.getId().equals(u.getId()))
                                  .findFirst();
        } else {
            existUser = userStorage.values()
                                   .stream()
                                   .filter(u -> user.getEmail().equals(u.getEmail()))
                                   .findFirst();
        }
        if (existUser.isPresent()) {
            throw new EntryAlreadyExists(String.format("Пользователь с email = %s уже существует", user.getEmail()));
        }
    }
}
