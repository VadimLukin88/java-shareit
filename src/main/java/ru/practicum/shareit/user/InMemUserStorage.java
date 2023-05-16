package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EntryAlreadyExists;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemUserStorage implements UserStorage {
    private Long userId = 0L;
    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.get(id);
    }

    @Override
    public User createUser(User user) {
        if (isEmailExist(user)) {
            throw new EntryAlreadyExists("Пользователь с таким email уже существует.");
        }
        if (user.getEmail() == null) {
            throw new ValidationException("Необходимо указать email пользователя.");
        }
        user.setId(++userId);
        userStorage.put(userId, user);
        return user;
    }

    @Override
    public User modifyUser(Long userId, User user) {
        user.setId(userId);
        if (userStorage.containsKey(userId)) {
            User oldUser = userStorage.get(userId);
            if (user.getName() != null) {
                oldUser.setName(user.getName());
            }
            if (user.getEmail() != null && !isEmailExist(user)) {
                oldUser.setEmail(user.getEmail());
            } else if (user.getEmail() != null && isEmailExist(user)) {
                throw new EntryAlreadyExists("Пользователь с таким email уже существует.");
            }
            userStorage.put(userId, oldUser);
        } else {
            throw new DataNotFoundException(String.format("Пользователь с Id = %s не найден!", userId));
        }
        return userStorage.get(userId);
    }

    @Override
    public void deleteUser(Long id) {
        if (userStorage.containsKey(id)) {
            userStorage.remove(id);
        } else {
            throw new DataNotFoundException(String.format("Пользователь с Id = %s не найден!", id));
        }
    }

    @Override
    public boolean isUserExist(Long id) {
        return userStorage.containsKey(id);
    }

    private boolean isEmailExist(User user){
        Optional<User> existUser = userStorage.values()
                .stream()
                .filter(u -> user.getEmail().equals(u.getEmail())).findFirst();
        if (existUser.isEmpty()) {
            return false;
        } else {
            return !user.getId().equals(existUser.get().getId());
        }
    }
}
