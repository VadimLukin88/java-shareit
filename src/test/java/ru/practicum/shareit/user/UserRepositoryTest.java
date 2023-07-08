package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testExistsUserByIdForExistsUser() {

        assertTrue(userRepository.existsUserById(1L), "Существующий пользователь не найден в репозитории");
        User savedUser = userRepository.getReferenceById(1L);

        assertEquals(1L, savedUser.getId(), "Возвращён некорректный Id пользователя");
    }

    @Test
    public void testExistsUserByIdForNotExistsUser() {
        assertFalse(userRepository.existsUserById(99L), "Неуществующий пользователь найден в репозитории");
    }

}