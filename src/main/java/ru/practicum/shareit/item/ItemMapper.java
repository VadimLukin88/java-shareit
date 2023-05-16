package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

@Component
public class ItemMapper {

    public ItemDto mapItemToDto (Item item){
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable(), item.getOwner());
    }

    public Item mapDtoToItem (ItemDto itemDto){
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getOwner(), new ItemRequest());
    }
}
