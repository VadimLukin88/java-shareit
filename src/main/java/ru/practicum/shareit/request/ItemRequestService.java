package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemReqRespDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemReqRespDto createItemRequest(Long requestorId, ItemRequestDto dto);

    List<ItemReqRespDto> getOwnItemRequest(Long requestorId);

    List<ItemReqRespDto> getAllItemRequest(int from, int size, Long requestorId);

    ItemReqRespDto getItemRequestById(Long requestId, Long requestorId);

}
