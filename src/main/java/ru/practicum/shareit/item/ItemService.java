package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);    // создаём вещь

    ItemDto modifyItem(Long userId, Long itemId, ItemDto itemDto);    // изменяем вещь

    ItemDto getItem(Long itemId);    // запрос вещи по Id

    List<ItemDto> findItems(String text);    // поиск вещи по названию/описанию

    List<ItemDto> getAllUserItems(Long userId);    // запрос всех вещей пользователя
}
