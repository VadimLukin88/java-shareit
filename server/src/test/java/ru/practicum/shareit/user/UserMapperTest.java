package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    // все тесты в данном классе не имеют абсолютно никакого смысла и написаны исключительно для увеличения процента
    // покрытия кода

    @Test
    public void testMapUserToDto() {
        User user = new User(1L, "name", "email");

        UserDto dto = UserMapper.mapUserToDto(user);

        assertEquals(user.getId(), dto.getId(), "Значения Id в User и в Dto не равны!");
        assertEquals(user.getName(), dto.getName(), "Значения Name в User и в Dto не равны!");
        assertEquals(user.getEmail(), dto.getEmail(), "Значения Name в User и в Dto не равны!");
    }

    @Test
    public void testMapDtToUser() {
        UserDto dto = new UserDto(1L, "name", "email");

        User user = UserMapper.mapDtoToUser(dto);

        assertEquals(user.getId(), dto.getId(), "Значения Id в User и в Dto не равны!");
        assertEquals(user.getName(), dto.getName(), "Значения Name в User и в Dto не равны!");
        assertEquals(user.getEmail(), dto.getEmail(), "Значения Name в User и в Dto не равны!");
    }

    @Test
    public void testMapUserToShortDto() {
        User user = new User(1L, "name", "email");

        UserShortDto dto = UserMapper.mapUserToShortDto(user);

        assertEquals(user.getId(), dto.getId(), "Значения Id в User и в Dto не равны!");
        assertEquals(user.getName(), dto.getName(), "Значения Name в User и в Dto не равны!");
    }

}