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
import static org.mockito.Mockito.*;

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
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 15, 17, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(BookingStatus.WAITING)
            .build();
        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        respDto = bookingService.createBooking(reqDto, 2L);

        assertNotNull(respDto, "Метод возвратил null");
        assertEquals(respDto.getId(), 1L, "Некорректный Id бронирования");
        assertEquals(respDto.getItem().getId(), 1L, "Некорректный Id вещи");
        assertEquals(respDto.getBooker().getId(), 2L, "Некорректный Id пользователя");

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // создание бронирования. Ошибки валидации дат
//    @Test
//    public void testCreateBookingWrongDate() {
//        // start time = end time
//        reqDto = BookingRequestDto.builder()
//            .id(1L)
//            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
//            .end(LocalDateTime.of(2023, 7, 5, 11, 0))
//            .itemId(1L)
//            .booker(UserMapper.mapUserToShortDto(user2))
//            .status(BookingStatus.WAITING)
//            .build();
//
//        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
//
//        assertThrows(ValidationException.class, () -> bookingService.createBooking(reqDto, 2L));
//
//        // start time > end time
//        reqDto.setEnd(LocalDateTime.of(2023, 6, 5, 11, 0));
//
//        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
//
//        assertThrows(ValidationException.class, () -> bookingService.createBooking(reqDto, 2L));
//
//        verify(itemRepository, times(0)).findById(anyLong());
//        verify(userRepository, times(0)).findById(anyLong());
//        verify(bookingRepository, times(0)).save(any(Booking.class));
//    }

    // создание бронирования. Бронирование на собственную вещь
    @Test
    public void testCreateBookingForOwnItem() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user1))
            .status(BookingStatus.WAITING)
            .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user1);

        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(reqDto, 1L));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(0)).findById(anyLong());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    // создание бронирования. Не найден пользователь
    @Test
    public void testCreateBookingUnknownUser() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(BookingStatus.WAITING)
            .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(reqDto, 2L));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    // создание бронирования. Не найдена вещь
    @Test
    public void testCreateBookingUnknownItem() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(BookingStatus.WAITING)
            .build();

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(reqDto, 2L));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(0)).findById(anyLong());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    // создание бронирования. Вещь недоступна для бронирования
    @Test
    public void testCreateBookingUnavailableItem() {
        item1.setAvailable(false);

        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(BookingStatus.WAITING)
            .build();
        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(reqDto, 2L));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(0)).findById(anyLong());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    // подтверждение бронирования. Нормальный сценарий
    @Test
    public void testApproveBooking() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(BookingStatus.WAITING)
            .build();

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

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // отклонение заявки на бронирование. Нормальный сценарий
    @Test
    public void testRejectBooking() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(null)
            .build();

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

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // подтверждение бронирования. Пользователь не является владельцем вещи
    @Test
    public void testApproveBookingWrongOwner() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(null)
            .build();
        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        assertThrows(DataNotFoundException.class, () -> bookingService.approveBooking(1L, 2L, true));

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    // подтверждение бронирования. Статус бронирования != WAITING
    @Test
    public void testApproveBookingWithWrongStatus() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(null)
            .build();

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 1L, true));

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    // получение бронирования по Id. Нормальный сценарий
    @Test
    public void voidTestGetBookingById() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(null)
            .build();

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        respDto = bookingService.getBookingById(1L, 1L);

        assertNotNull(respDto, "метод вернул null");
        assertEquals(respDto.getId(),1L, "Некорректный Id бронирования");
        assertEquals(respDto.getItem().getId(),1L, "Некорректный Id вещи");
        assertEquals(respDto.getBooker().getId(),2L, "Некорректный Id пользователя");

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    // получение бронирования по Id. Пользователь не является владельцем вещи или бронирования
    @Test
    public void voidTestGetBookingByIdWrongUser() {
        reqDto = BookingRequestDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 7, 5, 11, 0))
            .end(LocalDateTime.of(2023, 7, 10, 11, 0))
            .itemId(1L)
            .booker(UserMapper.mapUserToShortDto(user2))
            .status(null)
            .build();

        booking1 = BookingMapper.mapDtoToBooking(reqDto, item1, user2);
        booking1.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingById(1L, 5L));

        verify(bookingRepository, times(1)).findById(anyLong());
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

        verify(userRepository, times(6)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBooker(any(User.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findByBookerAndStartBeforeAndEndAfter(any(User.class),
                                                                                                        any(LocalDateTime.class),
                                                                                                        any(LocalDateTime.class),
                                                                                                        any(Pageable.class));
        verify(bookingRepository, times(1)).findByBookerAndEndBefore(any(User.class),
                                                                                           any(LocalDateTime.class),
                                                                                           any(Pageable.class));
        verify(bookingRepository, times(1)).findByBookerAndStartAfter(any(User.class),
                                                                                            any(LocalDateTime.class),
                                                                                            any(Pageable.class));
        verify(bookingRepository, times(2)).findByBookerAndStatus(any(User.class),
                                                                                        any(BookingStatus.class),
                                                                                        any(Pageable.class));
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

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(0)).findByBooker(any(User.class), any(Pageable.class));
        verify(bookingRepository, times(0)).findByBookerAndStartBeforeAndEndAfter(any(User.class),
                                                                                                        any(LocalDateTime.class),
                                                                                                        any(LocalDateTime.class),
                                                                                                        any(Pageable.class));
        verify(bookingRepository, times(0)).findByBookerAndEndBefore(any(User.class),
                                                                                           any(LocalDateTime.class),
                                                                                           any(Pageable.class));
        verify(bookingRepository, times(0)).findByBookerAndStartAfter(any(User.class),
                                                                                            any(LocalDateTime.class),
                                                                                            any(Pageable.class));
        verify(bookingRepository, times(0)).findByBookerAndStatus(any(User.class),
                                                                                        any(BookingStatus.class),
                                                                                        any(Pageable.class));
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

        verify(userRepository, times(6)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItem_Owner(any(User.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findByItem_OwnerAndStartBeforeAndEndAfter(any(User.class),
                                                                                                            any(LocalDateTime.class),
                                                                                                            any(LocalDateTime.class),
                                                                                                            any(Pageable.class));
        verify(bookingRepository, times(1)).findByItem_OwnerAndEndBefore(any(User.class),
                                                                                                any(LocalDateTime.class),
                                                                                                any(Pageable.class));
        verify(bookingRepository, times(1)).findByItem_OwnerAndStartAfter(any(User.class),
                                                                                                any(LocalDateTime.class),
                                                                                                any(Pageable.class));
        verify(bookingRepository, times(2)).findByItem_OwnerAndStatus(any(User.class),
                                                                                            any(BookingStatus.class),
                                                                                            any(Pageable.class));
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

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(0)).findByItem_Owner(any(User.class), any(Pageable.class));
        verify(bookingRepository, times(0)).findByItem_OwnerAndStartBeforeAndEndAfter(any(User.class),
                                                                                                            any(LocalDateTime.class),
                                                                                                            any(LocalDateTime.class),
                                                                                                            any(Pageable.class));
        verify(bookingRepository, times(0)).findByItem_OwnerAndEndBefore(any(User.class),
                                                                                                any(LocalDateTime.class),
                                                                                                any(Pageable.class));
        verify(bookingRepository, times(0)).findByItem_OwnerAndStartAfter(any(User.class),
                                                                                                any(LocalDateTime.class),
                                                                                                any(Pageable.class));
        verify(bookingRepository, times(0)).findByItem_OwnerAndStatus(any(User.class),
                                                                                            any(BookingStatus.class),
                                                                                            any(Pageable.class));
    }
}