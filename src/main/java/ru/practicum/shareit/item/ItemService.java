package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Long userId, ItemDto itemDto);    // создаём вещь

    Item modifyItem(Long userId, Long itemId, ItemDto itemDto);    // изменяем вещь

    Item getItem(Long userId, Long itemId);    // запрос вещи по Id

    List<Item> findItems(String text);    // поиск вещи по названию/описанию

    List<Item> getAllUserItems(Long userId);    // запрос всех вещей пользователя
}
