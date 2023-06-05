package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {    // создаём вещь
        userStorage.isUserExist(userId);
        itemDto.setOwner(userId);
        Item item = ItemMapper.mapDtoToItem(itemDto, userStorage.getUserById(userId));

        return ItemMapper.mapItemToDto(itemStorage.createItem(item));
    }

    public ItemDto modifyItem(Long userId, Long itemId, ItemDto itemDto) {    // изменяем вещь
        itemStorage.isItemExist(itemId);
        userStorage.isUserExist(userId);
        Long savedItemUserId = itemStorage.getItem(itemId).getOwner().getId();

        if (!userId.equals(savedItemUserId)) {
            throw new DataNotFoundException(String.format("Пользователь с Id = %s не является владельцем вещи с Id = %s", userId, itemId));
        }
        Item item = ItemMapper.mapDtoToItem(itemDto, userStorage.getUserById(userId));

        item.setId(itemId);
        return ItemMapper.mapItemToDto(itemStorage.modifyItem(item));
    }

    public ItemDto getItem(Long itemId) {    // запрос вещи по Id
        return ItemMapper.mapItemToDto(itemStorage.getItem(itemId));
    }

    public List<ItemDto> findItems(String text) {    // поиск вещи по названию/описанию
        if (text.isBlank()) return new ArrayList<>();   // если строка для поиска пустая, возвращаем пустой список
        return itemStorage.findItems(text).stream()
                                          .map(ItemMapper::mapItemToDto)
                                          .collect(Collectors.toList());
    }

    public List<ItemDto> getAllUserItems(Long userId) {    // запрос всех вещей пользователя
        userStorage.isUserExist(userId);
        return itemStorage.getAllUserItems(userId).stream()
                                                  .map(ItemMapper::mapItemToDto)
                                                  .collect(Collectors.toList());
    }
}
