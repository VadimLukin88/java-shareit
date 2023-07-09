package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;
    private Item item3;

    private Booking booking;
    private ItemRequest request;

    private ItemDto reqDto;      //Dto в реквесте
    private ItemDto respDto;     //Dto в респонсе

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
            itemRequestRepository);
        user1 = new User(1L, "name1", "email1@ya.ru");
        user2 = new User(2L, "name2", "email2@ya.ru");
        request = new ItemRequest(1L, "req_description", user2, LocalDateTime.now());
        item1 = new Item(1L, "name1", "description1", true, user1, null);
        item2 = new Item(2L, "name2", "description2", true, user1, request);
        item3 = new Item(3L, "name3", "description3", true, user2, null);
        booking = new Booking(1L,
            LocalDateTime.of(2023,7, 5, 11, 00),
            LocalDateTime.of(2023,7, 10, 11, 00),
            item1,
            user2,
            null);
        reqDto = null;
        respDto = null;
    }

    // создание вещи. Нормальный сценарий
    @Test
    public void testCreateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item1);

        item1.setRequest(request);
        reqDto = new ItemDto(1L,
                        "name1",
                    "description1",
                      true,
                        1L,
                    null,
                   null,
                     1L,
                     null);
        respDto = itemService.createItem(1L, reqDto);

        assertEquals(respDto.getId(), reqDto.getId(), "Id не совпадают");
        assertEquals(respDto.getName(), reqDto.getName(), "Name не совпадают");
        assertEquals(respDto.getDescription(), reqDto.getDescription(), "Description не совпадают");
        assertEquals(respDto.getAvailable(), reqDto.getAvailable(), "Available не совпадают");
        assertEquals(respDto.getOwner(), reqDto.getOwner(), "Owner не совпадают");
        assertNull(respDto.getLastBooking(), "LastBooking не равно Null");
        assertNull(respDto.getNextBooking(), "NextBooking не равно Null");
        assertEquals(respDto.getRequestId(), reqDto.getRequestId(), "RequestId не равны");
        assertNull(respDto.getComments(), "Comments не равно Null");
    }


    // создание вещи. Пользователь создающий вещь не найден
    @Test
    public void testCreateItemFailOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> itemService.createItem(1L, null));
    }

    // создание вещи. Указанный запрос на создание вещи не нейден
    @Test
    public void testCreateItemFailRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        reqDto = new ItemDto(1L,
                          "name1",
                      "description1",
                        true,
                          1L,
                      null,
                     null,
                       1L,
                       null);

        assertThrows(DataNotFoundException.class, () -> itemService.createItem(1L, reqDto));
    }

    // изменение данных вещи. Нормальный сценарий
    @Test
    public void testModifyItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class))).thenReturn(item1);

        reqDto = new ItemDto(1L,
            "name1",
            "description1",
            true,
            1L,
            null,
            null,
            null,
            null);
        respDto = itemService.modifyItem(1L, 1L, reqDto);

        assertEquals(respDto.getName(), reqDto.getName(), "Name не совпадают");
        assertEquals(respDto.getDescription(), reqDto.getDescription(), "Description не совпадают");
        assertEquals(respDto.getAvailable(), reqDto.getAvailable(), "Available не совпадают");
    }

    // изменение данных вещи. Вещь с указанным Id не найдена
    @Test
    public void testModifyItemUnknownItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> itemService.modifyItem(1L, 1L, null));
    }

    // получение вещи. Нормальный сценарий
    @Test
    public void testGetItem() {
        Comment comment = new Comment(1L, "text", item1, user2, LocalDateTime.now());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(commentRepository.findByItem_Id(anyLong())).thenReturn(List.of(comment));

        respDto = itemService.getItem(1L, 1L);

        assertNotNull(respDto, "Метод не возвратил Dto (возвращено null)");
        assertEquals(respDto.getId(), item1.getId(), "Id не равны");
        assertEquals(respDto.getName(), item1.getName(), "Name не равны");
        assertEquals(respDto.getAvailable(), item1.getAvailable(), "Available не равны");
        assertEquals(respDto.getDescription(), item1.getDescription(), "Description не равны");
        assertNull(respDto.getLastBooking(), "LastBooking не равен null");
        assertNull(respDto.getNextBooking(), "NextBooking не равен null");
        assertEquals(respDto.getComments().size(), 1, "Некорректный размер списка комментариев");
    }

    // получение вещи. Вещь с указанным Id не найдена.
    @Test
    public void testGetItemWrongId() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> itemService.getItem(1L, 1L));
    }

    // поиск вещи.
    @Test
    public void testFindItems() {
        when(itemRepository.findItemByNameOrDescriptionContainsAllIgnoreCaseAndAvailableIsTrue(anyString(), anyString()))
            .thenReturn(List.of(item1));

        List<ItemDto> dtoList = itemService.findItems("item1");

        assertEquals(dtoList.size(), 1, "Некорректный размер списка");
        assertEquals(dtoList.get(0), ItemMapper.mapItemToDto(item1), "Найденный объект не совпадает с исходным");

        dtoList = itemService.findItems("");
        assertEquals(dtoList.size(), 0, "Некорректный размер списка");
    }

    // получение всех вещей пользователя. Нормальный сценарий
    @Test
    public void testGetAllUserItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findItemByOwnerOrderById(any(User.class))).thenReturn(List.of(item1, item2));
        when(commentRepository.findByItem_Id(anyLong())).thenReturn(new ArrayList<>());

        List<ItemDto> dtoList = itemService.getAllUserItems(1L);

        ItemDto dto1 = ItemMapper.mapItemToDto(item1);

        ItemDto dto2 = ItemMapper.mapItemToDto(item2);

        dto1.setComments(new ArrayList<>());
        dto2.setComments(new ArrayList<>());
        assertEquals(dtoList.size(), 2L, "Некоррктный размер списка");
        assertEquals(dtoList.get(0), dto1, "Найденный объект не совпадает с исходным");
        assertEquals(dtoList.get(1), dto2, "Найденный объект не совпадает с исходным");
    }

    // получение всех вещей пользователя. Некорректный Id пользователя
    @Test
    public void testGetAllUserItemsWrongId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class,() -> itemService.getAllUserItems(1L));
    }

    // добавление комментария к вещи
    @Test
    public void testAddComment() {
        CommentRequestDto commentReqDto = new CommentRequestDto(1L, "text of comment", 1L, 1L, LocalDateTime.now());

        Comment comment = new Comment(1L, "text of comment", item1, user2, LocalDateTime.now());

        when(bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBeforeAndStatusOrderByStartDesc(anyLong(),
                                                                                                   anyLong(),
                                                                                                   any(LocalDateTime.class),
                                                                                                   any(BookingStatus.class)))
            .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto responseDto = itemService.addComment(2L, 1L, commentReqDto);

        assertNotNull(responseDto, "Метод не возвратил комментарий (возвращено null)");
        assertNotNull(responseDto.getCreated(), "Не указано время создания");
        assertEquals(responseDto.getId(), 1, "Некорректный Id комментария");
        assertEquals(responseDto.getAuthorName(), "name2", "Некорректно имя автора");
    }
}