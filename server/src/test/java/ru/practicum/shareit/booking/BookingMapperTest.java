package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    public void testMapDtoToBooking() {
        LocalDateTime start = LocalDateTime.of(2023, 7, 4, 22, 0);

        LocalDateTime end = LocalDateTime.of(2023, 7, 5, 22, 0);

        User user = new User(1L, "name", "email@ya.ru");

        UserShortDto userDto = UserMapper.mapUserToShortDto(user);

        BookingRequestDto requestDto = new BookingRequestDto(null, start, end, 1L, userDto, null);

        Item item = new Item(1L, "name", "description", true, user, null);

        Booking booking = BookingMapper.mapDtoToBooking(requestDto, item, user);

        booking.setId(1L);

        assertEquals(booking.getId(), 1L, "Id бронирования не равно 1");
        assertEquals(booking.getStart(), start, "Некорректное время начала бронирования");
        assertEquals(booking.getEnd(), end, "Некорректное время завершения бронирования");
        assertEquals(booking.getItem(), item, "В модели бронирования некорректный объект вещи");
        assertEquals(booking.getBooker(), user, "В модели бронирования некорректный объект арендатора");
        assertNull(booking.getStatus(), "Сатус бронирования не равен null");
    }

    @Test
    public void testMapBookingToDto() {
        LocalDateTime start = LocalDateTime.of(2023, 7, 4, 22, 0);

        LocalDateTime end = LocalDateTime.of(2023, 7, 5, 22, 0);

        User user = new User(1L, "name", "email@ya.ru");

        Item item = new Item(1L, "name", "description", true, user, null);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        BookingResponseDto dto = BookingMapper.mapBookingToDto(booking);

        assertEquals(dto.getId(), booking.getId(), "Id бронирования не равно 1");
        assertEquals(dto.getStart(), booking.getStart(), "Некорректное время начала бронирования");
        assertEquals(dto.getEnd(), booking.getEnd(), "Некорректное время завершения бронирования");
        assertEquals(dto.getItem(), ItemMapper.mapItemToShortDto(booking.getItem()),"В dto бронирования некорректный объект вещи");
        assertEquals(dto.getBooker(), UserMapper.mapUserToShortDto(booking.getBooker()),"В dto бронирования некорректный объект арендатора");
        assertEquals(dto.getStatus(), booking.getStatus(), "Сатус бронирования в Dto не равен статусу в модели");
    }

    @Test
    public void testMapBookingToShortDto() {
        LocalDateTime start = LocalDateTime.of(2023, 7, 4, 22, 0);

        LocalDateTime end = LocalDateTime.of(2023, 7, 5, 22, 0);

        User user = new User(1L, "name", "email@ya.ru");

        Item item = new Item(1L, "name", "description", true, user, null);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        BookingShortDto dto = BookingMapper.mapBookingToShortDto(booking);

        assertEquals(dto.getId(), booking.getId(), "Id бронирования не равно 1");
        assertEquals(dto.getBookerId(), booking.getBooker().getId(),"В dto бронирования Id арендатора не равен Id в модели");
    }

}