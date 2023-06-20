package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapItemToDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner().getId());
        return itemDto;
    }

    public static Item mapDtoToItem(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                        itemDto.getName(),
                        itemDto.getDescription(),
                        itemDto.getAvailable(),
                        null,
                       null);
    }

    public static ItemShortDto mapItemToShortDto(Item item) {
        return new ItemShortDto(item.getId(), item.getName());
    }
}
