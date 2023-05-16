package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemItemStorage implements ItemStorage {

    private Long itemId = 0L;
    private final Map<Long, Item> itemStorage = new HashMap<>();

    @Override
    public Item createItem(Item item) {    // создаём вещь
        item.setId(++itemId);
        itemStorage.put(itemId, item);
        return item;
    }

    @Override
    public Item modifyItem(Item item) {    // изменяем вещь
        long id = item.getId();
        if (itemStorage.containsKey(id)) {
            itemStorage.put(id, item);
        }
        return itemStorage.get(id);
    }

    @Override
    public Item getItem(Long itemId) {    // запрос вещи по Id
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> findItems(String text) {    // поиск вещи по названию/описанию
        String searchText = text.toLowerCase();

        List<Item> searchResult = new ArrayList<>(itemStorage.values().stream()
            .filter(item -> item.getName().toLowerCase().contains(searchText))
            .collect(Collectors.toList()));

        searchResult.addAll(itemStorage.values()
            .stream()
            .filter(item -> item.getDescription().toLowerCase().contains(searchText))
            .collect(Collectors.toList()));
        return searchResult;

    }

    @Override
    public List<Item> getAllUserItems(Long userId) {    // запрос всех вещей пользователя
        return itemStorage.values().stream()
            .filter(item -> item.getOwner().equals(userId))
            .collect(Collectors.toList());
    }

    @Override
    public boolean isExist(Long itemId) {
        return itemStorage.containsKey(itemId);
    }
}
