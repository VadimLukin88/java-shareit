package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemReqRespDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    public void testMapDtoToItemRequest() {
        LocalDateTime now = LocalDateTime.now();

        ItemRequestDto dto = new ItemRequestDto("description");

        User user = new User(1L, "name", "user@ya.ru");

        ItemRequest itemRequest = ItemRequestMapper.mapDtoToItemRequest(dto, user, now);

        itemRequest.setId(1L);

        assertEquals(itemRequest.getId(), 1L, "Id запроса не равен 1");
        assertEquals(itemRequest.getDescription(), dto.getDescription(), "Description запроса в модели в Dto не равны");
        assertEquals(itemRequest.getRequestor(), user, "Requestor в запросе не равен заданному");
        assertEquals(itemRequest.getCreated(), now, "Время создания запроса в модели и в Dto отличаются");
    }

    @Test
    public void testMapItemRequestToDto() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(1L, "name", "user@ya.ru");

        ItemRequest itemRequest = new ItemRequest(1L, "description", user, now);

        ItemReqRespDto dto = ItemRequestMapper.mapItemRequestToDto(itemRequest);

        assertEquals(itemRequest.getId(), dto.getId(), "Id запроса не равен 1");
        assertEquals(itemRequest.getDescription(), dto.getDescription(), "Description запроса в модели в Dto не равны");
        assertEquals(dto.getCreated(), now, "Время создания запроса в модели и в Dto отличаются");
        assertEquals(dto.getItems().size(), 0, "размер списка добавленных вещей больше 0");

    }

}