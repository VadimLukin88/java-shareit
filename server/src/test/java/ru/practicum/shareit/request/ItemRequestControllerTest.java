package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemReqRespDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private ItemRequestDto reqDto;
    private ItemReqRespDto respDto;

    @BeforeEach
    public void setUp() {
        reqDto = new ItemRequestDto("request description");
        respDto = ItemReqRespDto.builder()
            .id(1L)
            .description("request description")
            .created(LocalDateTime.now())
            .items(new ArrayList<>())
            .build();
    }

    // запрос на создание запроса вещи. Нормальный сценарий
    @Test
    public void testCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(respDto);

        mockMvc.perform(post("/requests")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.description", is("request description")))
            .andExpect(jsonPath("$.items", hasSize(0)));

        verify(itemRequestService, times(1)).createItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    // запрос на создание запроса вещи. Ошибки валидации RequestBody
//    @Test
//    public void testCreateItemRequestWithWrongBody() throws Exception {
//        reqDto.setDescription(null);
//
//        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(respDto);
//
//        mockMvc.perform(post("/requests")
//                .content(objectMapper.writeValueAsString(reqDto))
//                .header("X-Sharer-User-Id", 1L)
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//
//        reqDto.setDescription("");
//
//        mockMvc.perform(post("/requests")
//                .content(objectMapper.writeValueAsString(reqDto))
//                .header("X-Sharer-User-Id", 1L)
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//
//        verify(itemRequestService, times(0)).createItemRequest(anyLong(), any(ItemRequestDto.class));
//    }

    // запрос на создание запроса вещи. Ошибки в параметрах запроса
    @Test
    public void testCreateItemRequestWithWrongParameters() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(respDto);

        // запрос без JSON
        mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // запрос без заголовка X-Sharer-User-Id
        mockMvc.perform(post("/requests")
                .content(objectMapper.writeValueAsString(reqDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(itemRequestService, times(0)).createItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    // запрос на получение своих запросов вещей. Нормальный сценарий
    @Test
    public void testGetOwnItemRequest() throws Exception {
        when(itemRequestService.getOwnItemRequest(anyLong())).thenReturn(List.of(respDto));

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].description", is("request description")));

        verify(itemRequestService, times(1)).getOwnItemRequest(anyLong());
    }

    // запрос на получение своих запросов вещей. Ошибки в параметрах запроса
    @Test
    public void testGetOwnItemRequestWithWrongParameter() throws Exception {
        when(itemRequestService.getOwnItemRequest(anyLong())).thenReturn(List.of(respDto));

        // запрос без заголовкаX-Sharer-User-Id
        mockMvc.perform(get("/requests")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(itemRequestService, times(0)).getOwnItemRequest(anyLong());
    }

    // запрос на получение всех запросов вещей. Нормальный сценарий
    @Test
    public void testGetAllItemRequest() throws Exception {
        when(itemRequestService.getAllItemRequest(anyInt(), anyInt(), anyLong())).thenReturn(List.of(respDto));

        // запрос без заголовкаX-Sharer-User-Id
        mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", 1L)
                .param("from", "1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].description", is("request description")));

        verify(itemRequestService, times(1)).getAllItemRequest(anyInt(), anyInt(), anyLong());
    }

    // запрос на получение всех запросов вещей. Ошибки в параметре запроса
//    @Test
//    public void testGetAllItemRequestWithWrongParameter() throws Exception {
//        when(itemRequestService.getAllItemRequest(anyInt(), anyInt(), anyLong())).thenReturn(List.of(respDto));
//
//        // запрос без заголовкаX-Sharer-User-Id
//        mockMvc.perform(get("/requests/all")
//                .param("from", "1")
//                .param("size", "10")
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//
//        // индекс страницы отрицательный (from)
//        mockMvc.perform(get("/requests/all")
//                .header("X-Sharer-User-Id", 1L)
//                .param("from", "-1")
//                .param("size", "10")
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//
//        // размер страницы отрицательный (size)
//        mockMvc.perform(get("/requests/all")
//                .header("X-Sharer-User-Id", 1L)
//                .param("from", "1")
//                .param("size", "-10")
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//
//        // размер страницы равен 0 (size)
//        mockMvc.perform(get("/requests/all")
//                .header("X-Sharer-User-Id", 1L)
//                .param("from", "1")
//                .param("size", "-10")
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//
//        verify(itemRequestService, times(0)).getAllItemRequest(anyInt(), anyInt(), anyLong());
//    }

    // запрос на запроса вещи по Id. Нормальный сценарий
    @Test
    public void testGetItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(respDto);

        mockMvc.perform(get("/requests/1")
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.description", is("request description")));

        verify(itemRequestService, times(1)).getItemRequestById(anyLong(), anyLong());
    }

    // запрос на запроса вещи по Id. Нормальный сценарий
    @Test
    public void testGetItemRequestByIdWithWrongParameter() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(respDto);

        // запрос без заголовкаX-Sharer-User-Id
        mockMvc.perform(get("/requests/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // запрос с некорректным значением в PathParameter
        mockMvc.perform(get("/requests/null")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(itemRequestService, times(0)).getItemRequestById(anyLong(), anyLong());
    }
}