package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemRespDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemReqRespDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository,
                                  ItemRepository itemRepository,
                                  ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemReqRespDto createItemRequest(Long requestorId, ItemRequestDto dto) {
        LocalDateTime created = LocalDateTime.now();

        User requestor = userRepository.findById(requestorId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + requestorId + " не найден!"));

        ItemRequest itemRequest = ItemRequestMapper.mapDtoToItemRequest(dto, requestor, created);

        ItemRequest saved = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapItemRequestToDto(saved);
    }

    @Override
    public List<ItemReqRespDto> getOwnItemRequest(Long requestorId) {
        User requestor = userRepository.findById(requestorId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + requestorId + " не найден!"));

        List<ItemReqRespDto> reqDtoList = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(requestorId).stream()
            .map(ItemRequestMapper::mapItemRequestToDto)
            .collect(toList());

        List<Long> requestIds = reqDtoList.stream().map(ItemReqRespDto::getId).collect(toList());

        List<Item> items = itemRepository.findByRequest_IdIn(requestIds);

        Map<Long, List<ItemRespDto>> itemsByRequest = items.stream()
            .map(ItemMapper::mapItemToRespDto)
            .collect(Collectors.groupingBy(ItemRespDto::getRequestId, toList()));

        reqDtoList.stream().forEach(dto -> dto.setItems(itemsByRequest.getOrDefault(dto.getId(), new ArrayList<>())));

        return reqDtoList;
    }

    @Override
    public List<ItemReqRespDto> getAllItemRequest(int from, int size, Long requestorId) {
        Sort sort =  Sort.by("created").descending();

        Pageable pageable = PageRequest.of(from, size, sort);

        List<ItemReqRespDto> dtoList = itemRequestRepository.findByRequestor_IdNotOrderByCreatedDesc(pageable, requestorId)
            .stream()
            .map(ItemRequestMapper::mapItemRequestToDto)
            .collect(toList());

        List<Long> requestIds = dtoList.stream().map(ItemReqRespDto::getId).collect(toList());

        Map<Long, List<ItemRespDto>> itemsByRequest = itemRepository.findByRequest_IdIn(requestIds)
            .stream()
            .map(ItemMapper::mapItemToRespDto)
            .collect(Collectors.groupingBy(ItemRespDto::getRequestId, toList()));

        dtoList.stream().forEach(dto -> dto.setItems(itemsByRequest.getOrDefault(dto.getId(), new ArrayList<>())));
        return dtoList;
    }

    @Override
    public ItemReqRespDto getItemRequestById(Long requestId, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + requestorId + " не найден!"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
            .orElseThrow(() -> new DataNotFoundException("Запрос с Id = " + requestId + " не найден!"));

        ItemReqRespDto dto = ItemRequestMapper.mapItemRequestToDto(itemRequest);

        List<ItemRespDto> itemDto = itemRepository.findByRequest_Id(requestId).stream()
                            .map(ItemMapper::mapItemToRespDto)
                            .collect(toList());

        dto.setItems(itemDto);
        return dto;
    }
}
