package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private ItemDto dto;

    @BeforeEach
    public void setUp() {
        dto = new ItemDto(1L,
            "item1",
            "description1",
            true,
            1L,
            null,
            null,
            null,
            null);
    }

    // создание вещи. нормальный сценарий
    @Test
    public void testCreateItem() throws Exception {

        when(itemService.createItem(anyLong(), ArgumentMatchers.any(ItemDto.class))).thenReturn(dto);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("item1")))
            .andExpect(jsonPath("$.description", is("description1")));
    }

    // создание вещи. Имя вещи пустая строка или null
    @Test
    public void testCreateItemWithNameEmptyOrNull() throws Exception {
        dto.setName(null);

        when(itemService.createItem(anyLong(), ArgumentMatchers.any(ItemDto.class))).thenReturn(dto);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        dto.setName("");

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // создание вещи. Описание вещи пустая строка или null
    @Test
    public void testCreateItemWithDescriptionEmptyOrNull() throws Exception {
        dto.setDescription(null);
        when(itemService.createItem(anyLong(), ArgumentMatchers.any(ItemDto.class))).thenReturn(dto);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        dto.setDescription("");
        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // создание вещи. У вещи нет статуса бронирования
    @Test
    public void testCreateItemWithAvailableIsNull() throws Exception {
        dto.setAvailable(null);

        when(itemService.createItem(anyLong(), ArgumentMatchers.any(ItemDto.class))).thenReturn(dto);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // создание вещи. В запросе нет заголовка X-Sharer-User-Id
    @Test
    public void testCreateItemWithoutUserId() throws Exception {
        when(itemService.createItem(anyLong(), ArgumentMatchers.any(ItemDto.class))).thenReturn(dto);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // изменение вещи. Нормальный сценарий
    @Test
    public void testModifyItem() throws Exception {
        when(itemService.modifyItem(anyLong(), anyLong(), ArgumentMatchers.any(ItemDto.class))).thenReturn(dto);

        mockMvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("item1")))
            .andExpect(jsonPath("$.description", is("description1")));
    }

    // изменение вещи. Не найден пользователь/вещь
    @Test
    public void testModifyItemUserOrItemNotFound() throws Exception {

        when(itemService.modifyItem(anyLong(), anyLong(), ArgumentMatchers.any(ItemDto.class)))
            .thenThrow(new DataNotFoundException("Ошибка!"));

        mockMvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    // изменение вещи. В запросе не указан Id пользователя/Id вещи
    @Test
    public void testModifyItemNoUserIdOrItemId() throws Exception {
        when(itemService.modifyItem(anyLong(), anyLong(), ArgumentMatchers.any(ItemDto.class))).thenReturn(dto);
        // некорректный Id вещи
        mockMvc.perform(patch("/items/null")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // не указан заголовок X-Sharer-User-Id
        mockMvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // получение вещи по Id. Нормальный сценарий
    @Test
    public void testGetItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(dto);

        mockMvc.perform(get("/items/1")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("item1")))
            .andExpect(jsonPath("$.description", is("description1")));
    }

    // получение вещи по Id. Не найден пользователь или вещь
    @Test
    public void testGetItemWithWrongUserIdOrItemId() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenThrow(new DataNotFoundException("Ошибка!"));

        mockMvc.perform(get("/items/1")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    // получение вещи по Id. В запросе нет Id пользователя или Id вещи
    @Test
    public void testGetItemWithoutUserIdOrItemId() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(dto);

        mockMvc.perform(get("/items/null")
                .content(objectMapper.writeValueAsString(dto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        mockMvc.perform(get("/items/1")
                .content(objectMapper.writeValueAsString(dto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // поиск вещи по имени или описанию. Нормальный сценарий
    @Test
    public void testFindItems() throws Exception {
        when(itemService.findItems(anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/items/search")
                .param("text", "searchString")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)));
    }

    // поиск вещи по имени или описанию. Поиск по пустой строке
    @Test
    public void testFindItemsByEmptyString() throws Exception {
        when(itemService.findItems(anyString())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items/search")
                .param("text", "")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // поиск вещи по имени или описанию. Без обязтельного параметра в запросе
    @Test
    public void testFindItemsWithoutRequestParam() throws Exception {
        when(itemService.findItems(anyString())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items/search")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // Получение всех вещей рользователя. Нормальный сценарий
    @Test
    public void testGetAllUserItems() throws Exception {
        when(itemService.getAllUserItems(anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                 .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("item1")));
    }

    // Получение всех вещей рользователя. В запросе нет заголовка X-Sharer-User-Id
    @Test
    public void testGetAllUserItemsWithoutUserId() throws Exception {
        when(itemService.getAllUserItems(anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // Добавление комментария. Нормальный сценарий
    @Test
    public void testAddComment() throws Exception {
        CommentRequestDto reqDto = new CommentRequestDto(null, "comment", 1L, 2L, null);

        CommentResponseDto respDto = new CommentResponseDto(1L, "comment", "user2", LocalDateTime.now());

        when(itemService.addComment(anyLong(), anyLong(), ArgumentMatchers.any(CommentRequestDto.class))).thenReturn(respDto);

        mockMvc.perform(post("/items/1/comment")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(reqDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.text", is("comment")))
            .andExpect(jsonPath("$.created", notNullValue()));
    }

    // Добавление комментария. Ошибки в запросе
    @Test
    public void testAddCommentWrongRequest() throws Exception {
        CommentRequestDto reqDto = new CommentRequestDto(null, "comment", 1L, 2L, null);

        CommentResponseDto respDto = new CommentResponseDto(1L, "comment", "user2", LocalDateTime.now());

        when(itemService.addComment(anyLong(), anyLong(), ArgumentMatchers.any(CommentRequestDto.class))).thenReturn(respDto);

        // отсутствует RequestBody
        mockMvc.perform(post("/items/1/comment")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // отсутствует заголовок X-Sharer-User-Id
        mockMvc.perform(post("/items/1/comment")
                .content(objectMapper.writeValueAsString(reqDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // некоррестное значение в PathVariable
        mockMvc.perform(post("/items/null/comment")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(reqDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

}