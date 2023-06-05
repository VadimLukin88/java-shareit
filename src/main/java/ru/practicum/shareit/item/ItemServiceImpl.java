package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {    // создаём вещь
//        userRepository.isUserExist(userId);
        itemDto.setOwner(userId);
        Item item = ItemMapper.mapDtoToItem(itemDto, userRepository.getById(userId));

        return ItemMapper.mapItemToDto(itemRepository.save(item));
    }

    public ItemDto modifyItem(Long userId, Long itemId, ItemDto itemDto) {    // изменяем вещь
//        ItemRepository.isItemExist(itemId);
//        userRepository.isUserExist(userId);
        Long savedItemUserId = itemRepository.getById(itemId).getOwner().getId();

        if (!userId.equals(savedItemUserId)) {
            throw new DataNotFoundException(String.format("Пользователь с Id = %s не является владельцем вещи с Id = %s", userId, itemId));
        }
        Item item = ItemMapper.mapDtoToItem(itemDto, userRepository.getById(userId));

        item.setId(itemId);
        return ItemMapper.mapItemToDto(itemRepository.save(item));
    }

    public ItemDto getItem(Long itemId) {    // запрос вещи по Id
        return ItemMapper.mapItemToDto(itemRepository.getById(itemId));
    }

    public List<ItemDto> findItems(String text) {    // поиск вещи по названию/описанию
        if (text.isBlank()) return new ArrayList<>();   // если строка для поиска пустая, возвращаем пустой список
        return new ArrayList<>();
//        return ItemRepository.findItems(text).stream()
//                                          .map(ItemMapper::mapItemToDto)
//                                          .collect(Collectors.toList());
    }

    public List<ItemDto> getAllUserItems(Long userId) {    // запрос всех вещей пользователя
//        userRepository.isUserExist(userId);
//        return ItemRepository.getAllUserItems(userId).stream()
//                                                  .map(ItemMapper::mapItemToDto)
//                                                  .collect(Collectors.toList());
        return new ArrayList<>();
    }
}
