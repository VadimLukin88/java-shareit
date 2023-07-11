package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemReqRespDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;


    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private ItemRequestDto reqDto;
    private ItemReqRespDto respDto;


    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository);
        user = new User(1L, "name", "email@ya.ru");
        item = new Item(2L, "name", "description", true, user, itemRequest);
        itemRequest = new ItemRequest(1L,"description1", user, LocalDateTime.now());
        reqDto = new ItemRequestDto("description");
        respDto = null;
    }

    // создание запроса на вещь. Нормальный сценарий
    @Test
    public void testCreateItemRequest() {
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        respDto = itemRequestService.createItemRequest(1L, reqDto);

        assertEquals(respDto.getId(), 1L, "Некорректный Id запроса");
        assertEquals(respDto.getDescription(), "description1", "Некорректный Description запроса");
        assertEquals(respDto.getItems().size(), 0, "Некорректный размер списка Item в запросе");
        assertNotNull(respDto.getCreated(), "Некорректное время создания запроса");

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

    // создание запроса на вещь. Пользователь не найден
    @Test
    public void testCreateItemRequestUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new DataNotFoundException("Ошибка!"));

        assertThrows(DataNotFoundException.class, () -> itemRequestService.createItemRequest(1L, reqDto));

        verify(itemRequestRepository, times(0)).save(any(ItemRequest.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

    // получение собственных ItemRequest. Нормальный сценарий
    @Test
    public void testGetOwnItemRequest() {
        item.setRequest(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequest_IdIn(anyList())).thenReturn(List.of(item));

        List<ItemReqRespDto> dtoList = itemRequestService.getOwnItemRequest(1L);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка запросов");
        assertEquals(dtoList.get(0).getId(), 1, "Некорректный Id запроса");
        assertEquals(dtoList.get(0).getItems().size(), 1, "Некорректный размер списка вещей");
        assertEquals(dtoList.get(0).getItems().get(0).getId(), 2, "Некорректный Id вещи");

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findByRequestor_IdOrderByCreatedDesc(anyLong());
        verify(itemRepository, times(1)).findByRequest_IdIn(anyList());
    }

    // получение собственных ItemRequest. Неизвестный пользователь
    @Test
    public void testGetOwnItemRequestForUnknownUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> itemRequestService.getOwnItemRequest(1L));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(0)).findByRequestor_IdOrderByCreatedDesc(anyLong());
        verify(itemRepository, times(0)).findByRequest_Id(anyLong());
    }

    // получение всех ItemRequest. Нормальный сценарий
    @Test
    public void testGetAllItemRequest() {
        item.setRequest(itemRequest);

        when(itemRequestRepository.findByRequestor_IdNotOrderByCreatedDesc(any(Pageable.class), anyLong())).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequest_IdIn(anyList())).thenReturn(List.of(item));

        List<ItemReqRespDto> dtoList = itemRequestService.getAllItemRequest(1, 10, 1L);

        assertEquals(dtoList.size(), 1, "Некорректный размер списка запросов");
        assertEquals(dtoList.get(0).getId(), 1, "Некорректный Id запроса");
        assertEquals(dtoList.get(0).getItems().size(), 1, "Некорректный размер списка вещей");
        assertEquals(dtoList.get(0).getItems().get(0).getId(), 2, "Некорректный Id вещи");

        verify(itemRequestRepository, times(1)).findByRequestor_IdNotOrderByCreatedDesc(any(Pageable.class), anyLong());
        verify(itemRepository, times(1)).findByRequest_IdIn(anyList());
    }

    // получение ItemRequest по Id. Нормальный сценарий
    @Test
    public void testGetItemRequestById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequest_Id(anyLong())).thenReturn(List.of(item));

        respDto = itemRequestService.getItemRequestById(1L, 1L);

        assertEquals(respDto.getId(), 1L, "Некорректный ID запроса");
        assertEquals(respDto.getItems().size(), 1, "Некорректный размер списка вещей");
        assertEquals(respDto.getItems().get(0).getId(), 2, "Некорректный Id вещи");

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findByRequest_Id(anyLong());
   }

    // получение ItemRequest по Id. Пользователь не найден
    @Test
    public void testGetItemRequestByIdFromUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new DataNotFoundException("Ошибка!"));

        assertThrows(DataNotFoundException.class, () -> itemRequestService.getItemRequestById(1L,1L));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(0)).findById(anyLong());
        verify(itemRepository, times(0)).findByRequest_Id(anyLong());
    }
}