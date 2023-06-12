package ru.practicum.shareit.user;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        // Сначала хотел сделать проверки с помощью самодельного метода existsUserById в репозитории,
        // но потом решил, что лучше сократить кол-во запросов в базу и проверять существование пользователя через Optional
        // Метод existsUserById оставил только для удаления пользователя (там получилось 2 запроса к БД).
        return userRepository.findById(userId)
                             .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));
    }

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        User user = UserMapper.mapDtoToUser(userDto);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User modifyUser(Long userId, UserDto userDto) {
        User savedUser = userRepository.findById(userId)
                                       .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));

        User user = UserMapper.mapDtoToUser(userDto);

        if (user.getEmail() != null ) {
            savedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }
        return userRepository.save(savedUser);
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
