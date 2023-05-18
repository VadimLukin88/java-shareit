package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.itemMapper = itemMapper;
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {    // создаём вещь
        userStorage.isUserExist(userId);
        itemDto.setOwner(userId);
        Item item = itemMapper.mapDtoToItem(itemDto);

        return itemMapper.mapItemToDto(itemStorage.createItem(item));
    }

    public ItemDto modifyItem(Long userId, Long itemId, ItemDto itemDto) {    // изменяем вещь
        if (!itemStorage.isExist(itemId)) {
            throw new DataNotFoundException(String.format("Вещь с Id = %s не найдена", itemId));
        }
        userStorage.isUserExist(userId);
        Item savedItem = itemStorage.getItem(itemId);

        if (!userId.equals(savedItem.getOwner())) {
            throw new DataNotFoundException(String.format("Пользователь с Id = %s не является владельцем вещи с Id = %s", userId, itemId));
        }
        for (Map.Entry<String, Object> itemField : itemDto.toMap().entrySet()) {
            Object itemFieldValue = itemField.getValue();

            if (itemFieldValue != null) {
                switch (itemField.getKey()) {
                    case "name" :
                            savedItem.setName(itemFieldValue.toString());
                            break;
                    case "description" :
                            savedItem.setDescription(itemFieldValue.toString());
                            break;
                    case "available" :
                            savedItem.setAvailable((Boolean) itemFieldValue);
                            break;
                    case "owner":
                            Long id = (Long) itemFieldValue;
                            if (!id.equals(savedItem.getOwner())) {
                                throw new DataNotFoundException("Нельзя поменять владельца вещи.");
                            }
                            savedItem.setOwner(id);
                            break;
                    default:
                            break;
                }
            }
        }
        return itemMapper.mapItemToDto(itemStorage.modifyItem(savedItem));
    }

    public ItemDto getItem(Long itemId) {    // запрос вещи по Id
        return itemMapper.mapItemToDto(itemStorage.getItem(itemId));
    }

    public List<ItemDto> findItems(String text) {    // поиск вещи по названию/описанию
        if (text.isBlank()) return new ArrayList<>();   // если строка для поиска пустая, возвращаем пустой список
        return itemStorage.findItems(text).stream()
                                          .map(item -> itemMapper.mapItemToDto(item))
                                          .collect(Collectors.toList());
    }

    public List<ItemDto> getAllUserItems(Long userId) {    // запрос всех вещей пользователя
        userStorage.isUserExist(userId);
        return itemStorage.getAllUserItems(userId).stream()
                                                  .map(item -> itemMapper.mapItemToDto(item))
                                                  .collect(Collectors.toList());
    }
}
