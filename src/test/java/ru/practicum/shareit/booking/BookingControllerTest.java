package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private BookingRequestDto reqDto;
    private BookingResponseDto respDto;


    @BeforeEach
    public void setUp() {
        reqDto = new BookingRequestDto(null,
                                          LocalDateTime.now().plusHours(1L),
                                          LocalDateTime.now().plusDays(10L),
                                    1L,
                                          new UserShortDto(1L, "user2"),
                                    null);
        respDto = new BookingResponseDto(1L,
                                            LocalDateTime.now().plusHours(1L),
                                            LocalDateTime.now().plusDays(10L),
                                            new ItemShortDto(1L, "item1"),
                                            new UserShortDto(2L, "user2"),
                                            BookingStatus.WAITING);
    }

    // запрос на создание бронирования. Нормальный сценарий
    @Test
    public void testCreateBooking() throws Exception {

        when(bookingService.createBooking(reqDto, 2L)).thenReturn(respDto);

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 2)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.item.id", is(1)))
            .andExpect(jsonPath("$.booker.id", is(2)));
    }

    // запрос на создание бронирования. Ошибки валидации RequestBody
    @Test
    public void testCreateBookingWrongBody() throws Exception {
        reqDto.setId(1L);

        when(bookingService.createBooking(reqDto, 2L)).thenReturn(respDto);

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 2)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        reqDto.setId(null);
        reqDto.setStart(null);

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 2)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        reqDto.setStart(LocalDateTime.now().minusMinutes(5L));

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 2)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        reqDto.setStart(LocalDateTime.now().plusMinutes(5L));
        reqDto.setEnd(null);

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 2)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        reqDto.setEnd(LocalDateTime.now().minusMinutes(5L));

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 2)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        reqDto.setEnd(LocalDateTime.now().plusDays(5L));
        reqDto.setItemId(null);

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 2)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // запрос на создание бронирования. Отсутствует заголовок X-Sharer-User-Id
    @Test
    public void testCreateBookingWithoutUserId() throws Exception {

        when(bookingService.createBooking(reqDto, 2L)).thenReturn(respDto);

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                 .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // запрос на подтверждение бронирования. Нормальный сценарий
    @Test
    public void testApproveBooking() throws Exception {
        respDto.setStatus(APPROVED);

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(respDto);

        mockMvc.perform(patch("/bookings/1")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.item.id", is(1)))
            .andExpect(jsonPath("$.booker.id", is(2)))
            .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    // запрос на подтверждение бронирования. Отсутcтвуют обязательные элементы в запросе
    @Test
    public void testApproveBookingWrongRequest() throws Exception {

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(respDto);

        // без Id вещи в pathVariable
        mockMvc.perform(patch("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(405));

        // без заголовка X-Sharer-User-Id
        mockMvc.perform(patch("/bookings/1")
                .content(objectMapper.writeValueAsString(reqDto))
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // без requestParam с true/false
        mockMvc.perform(patch("/bookings/1")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // получение бронирования по Id. Нормальный сценарий
    @Test
    public void testGetBookingById() throws Exception {
        respDto.setStatus(APPROVED);

        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(respDto);

        mockMvc.perform(get("/bookings/1")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.item.id", is(1)))
            .andExpect(jsonPath("$.booker.id", is(2)))
            .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    // получение бронирования по Id. Отсутcтвуют обязательные элементы в запросе
    @Test
    public void testGetBookingByIdWrongRequest() throws Exception {
        respDto.setStatus(APPROVED);

        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(respDto);

        // некорректный Id вещи в pathVariable
        mockMvc.perform(get("/bookings/null")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // без заголовка X-Sharer-User-Id
        mockMvc.perform(get("/bookings/1")
                .content(objectMapper.writeValueAsString(reqDto))
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // запрос на получение всех бронирований пользователя. Нормальный сценарий
    @Test
    public void testGetBookingByUser() throws Exception {
        respDto.setStatus(APPROVED);

        when(bookingService.getBookingByUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                           .thenReturn(List.of(respDto));

        mockMvc.perform(get("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].item.id", is(1)))
            .andExpect(jsonPath("$[0].booker.id", is(2)))
            .andExpect(jsonPath("$[0].status", is("APPROVED")));
    }

    // запрос на получение всех бронирований пользователя. Ошибки валидации параметров запроса
    @Test
    public void testGetBookingByUserWrongRequest() throws Exception {
        respDto.setStatus(APPROVED);

        when(bookingService.getBookingByUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
            .thenReturn(List.of(respDto));

        // отрицательный индекс страницы (from)
        mockMvc.perform(get("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", "-1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // отрицательный размер страницы (size)
        mockMvc.perform(get("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "-10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // нулевой размер страницы (size)
        mockMvc.perform(get("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "0")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // некорректный статус (state)
        mockMvc.perform(get("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "BLABLA")
                .param("from", "1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // отсутствует Id пользователя (заголовок X-Sharer-User-Id)
        mockMvc.perform(get("/bookings")
                .content(objectMapper.writeValueAsString(reqDto))
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // запрос на получение всех бронирований для владельца вещей. Нормальный сценарий
    @Test
    public void testGetAllBookingOfOwner() throws Exception {
        respDto.setStatus(APPROVED);

        when(bookingService.getAllBookingOfOwner(anyLong(), any(BookingState.class), anyInt(), anyInt()))
            .thenReturn(List.of(respDto));

        mockMvc.perform(get("/bookings/owner")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].item.id", is(1)))
            .andExpect(jsonPath("$[0].booker.id", is(2)))
            .andExpect(jsonPath("$[0].status", is("APPROVED")));
    }

    // запрос на получение всех бронирований для владельца вещей. Ошибки валидации параметров запроса
    @Test
    public void testGetAllBookingOfOwnerWrongRequest() throws Exception {
        respDto.setStatus(APPROVED);

        when(bookingService.getAllBookingOfOwner(anyLong(), any(BookingState.class), anyInt(), anyInt()))
            .thenReturn(List.of(respDto));

        // отрицательный индекс страницы (from)
        mockMvc.perform(get("/bookings/owner")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", "-1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // отрицательный размер страницы (size)
        mockMvc.perform(get("/bookings/owner")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "-10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // нулевой размер страницы (size)
        mockMvc.perform(get("/bookings/owner")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "0")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // некорректный статус (state)
        mockMvc.perform(get("/bookings/owner")
                .content(objectMapper.writeValueAsString(reqDto))
                .header("X-Sharer-User-Id", 1)
                .param("state", "BLABLA")
                .param("from", "1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // отсутствует Id пользователя (заголовок X-Sharer-User-Id)
        mockMvc.perform(get("/bookings/owner")
                .content(objectMapper.writeValueAsString(reqDto))
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}