package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    // штатное создание user
    @Test
    public void testCreateUser() {
        UserDto requestDto = new UserDto(null,"name1", "email1@ya.ru");

        User user = UserMapper.mapDtoToUser(requestDto);

        user.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto responseDto = userService.createUser(requestDto);

        assertEquals(1L, responseDto.getId(), "Возвращён некорректный Id для User");
        assertEquals("name1", responseDto.getName(), "Возвращено некорректное name для User");
        assertEquals("email1@ya.ru", responseDto.getEmail(), "Возвращено некорректный email для User");
    }

    // штатное изменение данных пользователя
    @Test
    public void testModifyExistUser() {
        User savedUser = new User(1L, "oldName", "oldEmail");

        UserDto changedUserDto = new UserDto(null, "newName", "newEmail");

        User changedUser = UserMapper.mapDtoToUser(changedUserDto);

        changedUser.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(changedUser);
        UserDto responseDto = userService.modifyUser(1L, changedUserDto);

        assertEquals(1L, responseDto.getId(), "Возвращён некорректный Id для User");
        assertEquals("newName", responseDto.getName(), "Возвращено некорректное name для User");
        assertEquals("newEmail", responseDto.getEmail(), "Возвращено некорректный email для User");
    }

    // модификация несуществующего пользователя
    @Test
    public void testModifyNotExistUser() {
        UserDto changedUserDto = new UserDto(null, "newName", "newEmail");

        when(userRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> userService.modifyUser(1L, changedUserDto));
    }

    // тестировать нормально поведение метода deleteUser таким способом не имеет смысла

    // удаление несуществующего пользователя
    @Test
    public void testDeleteNotExistUser() {
        when(userRepository.existsUserById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> userService.deleteUser(1L));
    }

}