package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    public void testMapItemToDto() {
        User user = new User(1L, "user1", "user@ya.ru");

        Item item = new Item(1L, "item1", "description", true, user, null);

        ItemDto dto = ItemMapper.mapItemToDto(item);

        assertEquals(dto.getId(), item.getId(), "Id в модели и в Dto не равны");
        assertEquals(dto.getName(), item.getName(), "Name в модели и в Dto не равны");
        assertEquals(dto.getDescription(), item.getDescription(), "Description в модели и в Dto не равны");
        assertEquals(dto.getAvailable(), item.getAvailable(), "Available в модели и в Dto не равны");
        assertEquals(dto.getOwner(), item.getOwner().getId(), "Id владельца в модели и в Dto не равны");
        assertNull(dto.getRequestId(), "Id запроса на добавление вещи в Dto не равен null");
        assertNull(dto.getLastBooking(), "LastBooking в Dto не равен null");
        assertNull(dto.getNextBooking(), "NextBooking в Dto не равен null");
        assertNull(dto.getComments(), "Comments в Dto не равен null");
    }

    @Test
    public void testMapDtoToItem() {
        ItemDto dto = new ItemDto(1L, "item1", "description", true, 2L, null, null, 1L, null);

        Item item = ItemMapper.mapDtoToItem(dto);

        assertEquals(dto.getId(), item.getId(), "Id в модели и в Dto не равны");
        assertEquals(dto.getName(), item.getName(), "Name в модели и в Dto не равны");
        assertEquals(dto.getDescription(), item.getDescription(), "Description в модели и в Dto не равны");
        assertEquals(dto.getAvailable(), item.getAvailable(), "Available в модели и в Dto не равны");
        assertNull(item.getOwner(), "Id владельца в модели и в Dto не равны");
        assertNull(item.getRequest(), "Id запроса на добавление вещи в Dto не равен null");
    }

    @Test
    public void testMapItemToRespDto() {
        User user = new User(1L, "user1", "user@ya.ru");

        ItemRequest request = new ItemRequest(1L, "description", user, LocalDateTime.now());

        Item item = new Item(1L, "item1", "description", true, user, request);

        ItemRespDto dto = ItemMapper.mapItemToRespDto(item);

        assertEquals(dto.getId(), item.getId(), "Id в модели и в Dto не равны");
        assertEquals(dto.getName(), item.getName(), "Name в модели и в Dto не равны");
        assertEquals(dto.getOwnerId(), item.getOwner().getId(), "Id владельца в модели и в Dto не равны");
        assertEquals(dto.getDescription(), item.getDescription(), "Description в модели и в Dto не равны");
        assertEquals(dto.isAvailable(), item.getAvailable(), "Available в модели и в Dto не равны");
        assertEquals(dto.getRequestId(), item.getRequest().getId(), "Id запроса в модели и в Dto не равны");
    }

    @Test
    public void testMapItemToShortDto() {
        User user = new User(1L, "user1", "user@ya.ru");

        ItemRequest request = new ItemRequest(1L, "description", user, LocalDateTime.now());

        Item item = new Item(1L, "item1", "description", true, user, request);

        ItemShortDto dto = ItemMapper.mapItemToShortDto(item);

        assertEquals(dto.getId(), item.getId(), "Id в модели и в Dto не равны");
        assertEquals(dto.getName(), item.getName(), "Name в модели и в Dto не равны");
    }
}