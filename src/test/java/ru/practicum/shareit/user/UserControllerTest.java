package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // возвращаем пустой список всех пользователей
    @Test
    public void testGetAllUsersEmpty() throws Exception {
        when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // возвращаем НЕ пустой список всех пользователей
    @Test
    public void testGetAllUsersNotEmpty() throws Exception {
        UserDto dto1 = new UserDto(1L, "name1", "email1");
        UserDto dto2 = new UserDto(2L, "name2", "email2");

        when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)));
    }

    // возвращаем данные пользователя с определённым Id
    @Test
    public void testGetExistUserById() throws Exception {
        UserDto dto1 = new UserDto(1L, "name1", "email1");

        when(userService.getUserById(anyLong())).thenReturn(dto1);

        mockMvc.perform(get("/users/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("name1")))
            .andExpect(jsonPath("$.email", is("email1")));
    }

    // запрашиваем пользователя с несуществующим Id
    @Test
    public void testGetNotExistUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new DataNotFoundException("Не найден"));

        mockMvc.perform(get("/users/99")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // создание пользователя. Нормальный сценарий
    @Test
    public void testCreateUser()  throws Exception {
        UserDto dto1 = new UserDto(null, "name1", "email@ya.ru");
        UserDto dto2 = new UserDto(1L, "name1", "email@ya.ru");

        when(userService.createUser(any(UserDto.class))).thenReturn(dto2);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(dto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("name1")))
            .andExpect(jsonPath("$.email", is("email@ya.ru")));
    }

    // создание пользователя с пустым именем
    @Test
    public void testCreateUserWithEmptyName()  throws Exception {
        UserDto dto1 = new UserDto(1L, "", "email@ya.ru");

        when(userService.createUser(any(UserDto.class))).thenReturn(dto1);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(dto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // создание пользователя с пустым email
    @Test
    public void testCreateUserWithEmptyEmail()  throws Exception {
        UserDto dto1 = new UserDto(1L, "name1", "");

        when(userService.createUser(any(UserDto.class))).thenReturn(dto1);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(dto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // создание пользователя с некорректным email
    @Test
    public void testCreateUserWithIncorrectEmail() throws Exception {
        UserDto dto1 = new UserDto(1L, "name1", "email1");

        when(userService.createUser(any(UserDto.class))).thenReturn(dto1);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(dto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // изменение данных пользователя. Нормальный сценарий
    @Test
    public void testModifyUser() throws Exception {
        UserDto dto1 = new UserDto(1L, "name1", "email@ya.ru");

        when(userService.modifyUser(anyLong(), any(UserDto.class))).thenReturn(dto1);

        mockMvc.perform(patch("/users/1")
                .content(objectMapper.writeValueAsString(dto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("name1")))
            .andExpect(jsonPath("$.email", is("email@ya.ru")));
    }

    // изменение данных пользователя. Указан некорректный email
    @Test
    public void testModifyUserWithIncorrectEmail() throws Exception {
        UserDto dto1 = new UserDto(1L, "name1", "email1");

        when(userService.modifyUser(anyLong(), any(UserDto.class))).thenReturn(dto1);

        mockMvc.perform(patch("/users/1")
                .content(objectMapper.writeValueAsString(dto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // изменение данных пользователя. Указан несуществующий Id пользователя
    @Test
    public void testModifyUserWithWrongId() throws Exception {
        UserDto dto1 = new UserDto(99L, "name1", "email@ya.ru");

        when(userService.modifyUser(anyLong(), any(UserDto.class))).thenThrow(new DataNotFoundException("Не найден"));

        mockMvc.perform(patch("/users/99")
                .content(objectMapper.writeValueAsString(dto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    // удаление пользователя. Нормальный сценарий
    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    // удаление пользователя с несуществующим Id
    @Test
    public void testDeleteUserWithWrongId() throws Exception {
        doThrow(new DataNotFoundException("Пользователь не найден")).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/99")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

}