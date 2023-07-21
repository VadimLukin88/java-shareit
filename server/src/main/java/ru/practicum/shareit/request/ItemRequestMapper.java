package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemReqRespDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest mapDtoToItemRequest(ItemRequestDto dto,
                                                  User requestor,
                                                  LocalDateTime created) {
        return new ItemRequest(null,
                                dto.getDescription(),
                                requestor,
                                created);
    }

    public static ItemReqRespDto mapItemRequestToDto(ItemRequest itemRequest) {
        return new ItemReqRespDto(itemRequest.getId(),
                                  itemRequest.getDescription(),
                                  itemRequest.getCreated(),
                                  new ArrayList<>());
    }

}
