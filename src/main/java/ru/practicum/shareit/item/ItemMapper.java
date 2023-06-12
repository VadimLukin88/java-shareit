package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapItemToDto(Item item) {
        return new ItemDto(item.getId(),
                           item.getName(),
                           item.getDescription(),
                           item.getAvailable(),
                           item.getOwner().getId(),
                           BookingMapper.mapBookingToShortDto(item.getLastBooking()),
                           BookingMapper.mapBookingToShortDto(item.getNextBooking()));
    }

    public static Item mapDtoToItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(),
                        itemDto.getName(),
                        itemDto.getDescription(),
                        itemDto.getAvailable(),
                        user,
                       null,
                     null,
                    null,
                     null);
    }

    public static ItemResponseDto mapItemToResponseDto(Item item) {
        return new ItemResponseDto(item.getId(), item.getName());
    }
}
