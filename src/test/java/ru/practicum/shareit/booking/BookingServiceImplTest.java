package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;

    private Booking booking1;

    private BookingRequestDto reqDto;
    private BookingResponseDto respDto;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

        user1 = new User(1L, "name1", "email1@ya.ru");
        user2 = new User(2L, "name2", "email2@ya.ru");
        item1 = new Item(1L, "name1", "description1", true, user1, null);
        booking1 = null;
        reqDto = null;
        respDto = null;
    }

    // создание бронирования. Нормальный сценарий
    @Test
    public void testCreateBooking() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 15, 17, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            BookingStatus.WAITING);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        respDto = bookingService.createBooking(reqDto, 2L);

        assertNotNull(respDto, "Метод возвратил null");
        assertEquals(respDto.getId(), 1L, "Некорректный Id бронирования");
        assertEquals(respDto.getItem().getId(), 1L, "Некорректный Id вещи");
        assertEquals(respDto.getBooker().getId(), 2L, "Некорректный Id пользователя");

    }

    // создание бронирования. Ошибки валидации дат
    @Test
    public void testCreateBookingWrongDate() {
        // start time = end time
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 5, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            BookingStatus.WAITING);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(reqDto, 2L));

        // start time > end time
        reqDto.setEnd(LocalDateTime.of(2023, 6, 5, 11, 0));

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(reqDto, 2L));
    }

    // создание бронирования. Бронирование на собственную вещь
    @Test
    public void testCreateBookingForOwnItem() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user1),
            BookingStatus.WAITING);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));


        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user1);

        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(reqDto, 1L));
    }

    // создание бронирования. Не найден пользователь
    @Test
    public void testCreateBookingUnknownUser() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            BookingStatus.WAITING);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(reqDto, 2L));
    }

    // создание бронирования. Не найдена вещь
    @Test
    public void testCreateBookingUnknownItem() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            BookingStatus.WAITING);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(reqDto, 2L));
    }

    // создание бронирования. Вещь недоступна для бронирования
    @Test
    public void testCreateBookingUnavailableItem() {
        item1.setAvailable(false);

        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            BookingStatus.WAITING);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(reqDto, 2L));
    }

    // подтверждение бронирования. Нормальный сценарий
    @Test
    public void testApproveBooking() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            null);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.WAITING);

        Booking savedBooking = new Booking(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            item1,
            user2,
            BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        respDto = bookingService.approveBooking(1L, 1L, true);

        assertNotNull(respDto, "Метод вернул null");
        assertEquals(respDto.getId(), 1L, "Некорректный Id бронирования");
        assertEquals(respDto.getItem().getId(), 1L, "Некорректный Id вещи");
        assertEquals(respDto.getBooker().getId(), 2L, "Некорректный Id пользователя");
        assertEquals(respDto.getStatus(), BookingStatus.APPROVED, "Некорректный статус");
    }

    // отклонение заявки на бронирование. Нормальный сценарий
    @Test
    public void testRejectBooking() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            null);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.WAITING);

        Booking savedBooking = new Booking(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            item1,
            user2,
            BookingStatus.REJECTED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        respDto = bookingService.approveBooking(1L, 1L, false);

        assertNotNull(respDto, "Метод вернул null");
        assertEquals(respDto.getId(), 1L, "Некорректный Id бронирования");
        assertEquals(respDto.getItem().getId(), 1L, "Некорректный Id вещи");
        assertEquals(respDto.getBooker().getId(), 2L, "Некорректный Id пользователя");
        assertEquals(respDto.getStatus(), BookingStatus.REJECTED, "Некорректный статус");
    }

    // подтверждение бронирования. Пользователь не является владельцем вещи
    @Test
    public void testApproveBookingWrongOwner() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            null);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        assertThrows(DataNotFoundException.class, () -> bookingService.approveBooking(1L, 2L, true));
    }

    // подтверждение бронирования. Статус бронирования != WAITING
    @Test
    public void testApproveBookingWithWrongStatus() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            null);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 1L, true));
    }

    // получение бронирования по Id. Нормальный сценарий
    @Test
    public void voidTestGetBookingById() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            null);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        respDto = bookingService.getBookingById(1L, 1L);

        assertNotNull(respDto, "метод вернул null");
        assertEquals(respDto.getId(),1L, "Некорректный Id бронирования");
        assertEquals(respDto.getItem().getId(),1L, "Некорректный Id вещи");
        assertEquals(respDto.getBooker().getId(),2L, "Некорректный Id пользователя");
    }

    // получение бронирования по Id. Пользователь не является владельцем вещи или бронирования
    @Test
    public void voidTestGetBookingByIdWrongUser() {
        reqDto = new BookingRequestDto(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            1L,
            UserMapper.mapUserToShortDto(user2),
            null);

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingById(1L, 5L));
    }

    // получение всех бронирований пользователя. Нормальный сценарий
    @Test
    public void testGetBookingByUser() {
        booking1 = new Booking(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            item1,
            user2,
            BookingStatus.APPROVED);


        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBooker(any(User.class), any(Pageable.class))).thenReturn(List.of(booking1));
        when(bookingRepository.findByBookerAndStartBeforeAndEndAfter(any(User.class),
                                                                     any(LocalDateTime.class),
                                                                     any(LocalDateTime.class),
                                                                     any(Pageable.class)))
                               .thenReturn(List.of(booking1));
        when(bookingRepository.findByBookerAndEndBefore(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                               .thenReturn(List.of(booking1));
        when(bookingRepository.findByBookerAndStartAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                               .thenReturn(List.of(booking1));
        when(bookingRepository.findByBookerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                               .thenReturn(List.of(booking1));

        List<BookingResponseDto> dtoList = bookingService.getBookingByUser(2L, BookingState.ALL, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getBookingByUser(2L, BookingState.CURRENT, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getBookingByUser(2L, BookingState.FUTURE, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getBookingByUser(2L, BookingState.PAST, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getBookingByUser(2L, BookingState.WAITING, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getBookingByUser(2L, BookingState.REJECTED, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");
    }

    // получение всех бронирований пользователя. Неизвестный пользователь
    @Test
    public void testGetBookingByUserUnknown() {
        booking1 = new Booking(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            item1,
            user2,
            BookingStatus.APPROVED);


        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingByUser(10L, BookingState.ALL, 1, 10));
    }

    // получение всех бронирований по владельцу. Нормальный сценарий
    @Test
    public void testGetAllBookingOfOwner() {
        booking1 = new Booking(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            item1,
            user2,
            BookingStatus.APPROVED);


        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItem_Owner(any(User.class), any(Pageable.class))).thenReturn(List.of(booking1));
        when(bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfter(any(User.class),
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            any(Pageable.class)))
            .thenReturn(List.of(booking1));
        when(bookingRepository.findByItem_OwnerAndEndBefore(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
            .thenReturn(List.of(booking1));
        when(bookingRepository.findByItem_OwnerAndStartAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
            .thenReturn(List.of(booking1));
        when(bookingRepository.findByItem_OwnerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
            .thenReturn(List.of(booking1));

        List<BookingResponseDto> dtoList = bookingService.getAllBookingOfOwner(1L, BookingState.ALL, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getAllBookingOfOwner(1L, BookingState.CURRENT, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getAllBookingOfOwner(1L, BookingState.FUTURE, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getAllBookingOfOwner(1L, BookingState.PAST, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getAllBookingOfOwner(1L, BookingState.WAITING, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");

        dtoList = bookingService.getAllBookingOfOwner(1L, BookingState.REJECTED, 1,10);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0).getId(), 1L, "Некорректный Id бронирования");
    }

    // получение всех бронирований по владельцу. Неизвестный пользователь
    @Test
    public void testGetAllBookingOfOwnerUnknown() {
        booking1 = new Booking(1L,
            LocalDateTime.of(2023, 7, 5, 11, 0),
            LocalDateTime.of(2023, 7, 10, 11, 0),
            item1,
            user2,
            BookingStatus.APPROVED);


        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.getAllBookingOfOwner(10L, BookingState.ALL, 1, 10));
    }
}