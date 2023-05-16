package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);    // создаём вещь

    Item modifyItem(Item item);    // изменяем вещь

    Item getItem(Long itemId);    // запрос вещи по Id

    List<Item> findItems(String text);    // поиск вещи по названию/описанию

    List<Item> getAllUserItems(Long userId);    // запрос всех вещей пользователя

    boolean isExist(Long itemId);
}
