package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;  // подключим здесь сервис, чтобы не писать по новой проверки на существование юзера
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }
    @Override
    @Transactional
    public Item createItem(Long userId, ItemDto itemDto) {    // создаём вещь
        Item item = ItemMapper.mapDtoToItem(itemDto, userService.getUserById(userId));

        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item modifyItem(Long userId, Long itemId, ItemDto itemDto) {    // изменяем вещь

        Item savedItem = itemRepository.findById(itemId)
                                       .orElseThrow(() -> new DataNotFoundException("Вещь с Id = " + itemId + " не найдена!"));

        if (!userId.equals(savedItem.getOwner().getId())) {
            throw new DataNotFoundException(String.format("Пользователь с Id = %s не является владельцем вещи с Id = %s", userId, itemId));
        }
        Item item = ItemMapper.mapDtoToItem(itemDto, userService.getUserById(userId));

        if (item.getName() != null) {
            savedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            savedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }
        if (item.getOwner() != null) {
            savedItem.setOwner(item.getOwner());
        }
        if (item.getRequest() != null) {
            savedItem.setRequest(item.getRequest());
        }
        return itemRepository.save(savedItem);
    }

    @Override
    public Item getItem(Long userId, Long itemId) {    // запрос вещи по Id
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new DataNotFoundException("Вещь с Id = " + itemId + " не найдена!"));

        LocalDateTime rightNow = LocalDateTime.now();

        if (userId.equals(item.getOwner().getId())) {
            item.setLastBooking(bookingRepository.findFirstByItem_OwnerAndStartBeforeOrderByStartDesc(item.getOwner(), rightNow));
            item.setNextBooking(bookingRepository.findFirstByItem_OwnerAndStartAfterOrderByStartDesc(item.getOwner(), rightNow));
        }

        return item;
    }

    @Override
    public List<Item> findItems(String text) {    // поиск вещи по названию/описанию
        if (text.isBlank()) return new ArrayList<>();   // если строка для поиска пустая, возвращаем пустой список
        return itemRepository.findItemByNameOrDescriptionContainsAllIgnoreCaseAndAvailableIsTrue(text, text);
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {    // запрос всех вещей пользователя
        User owner = userService.getUserById(userId);

        List<Item> itemList = itemRepository.findItemByOwner(owner);

        LocalDateTime rightNow = LocalDateTime.now();

        for (Item item : itemList) {
            item.setLastBooking(bookingRepository.findFirstByItem_OwnerAndStartBeforeOrderByStartDesc(owner, rightNow));
            item.setNextBooking(bookingRepository.findFirstByItem_OwnerAndStartAfterOrderByStartDesc(owner, rightNow));
        }
        return itemList;
    }
}
