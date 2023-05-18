package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
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
        SortedSet<Item> result = new TreeSet<>(Comparator.comparingLong(Item::getId));

        result.addAll(itemStorage.values().stream()     // ищем в названии/имени
            .filter(item -> item.getName().toLowerCase().contains(searchText))
            .filter(Item::isAvailable)
            .collect(Collectors.toSet()));

        result.addAll(itemStorage.values()  // ищем в описании
            .stream()
            .filter(item -> item.getDescription().toLowerCase().contains(searchText))
            .filter(Item::isAvailable)
            .collect(Collectors.toSet()));

        return new ArrayList<Item>(result);

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
