package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {    // создаём вещь
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));

        Item item = ItemMapper.mapDtoToItem(itemDto);

        if (itemDto.getRequestId() != null) {
            long reqId = itemDto.getRequestId();

            ItemRequest itemRequest = itemRequestRepository.findById(reqId)
                .orElseThrow(() -> new DataNotFoundException("Запрос с Id = " + reqId + " не найден!"));

            item.setRequest(itemRequest);
        }

        item.setOwner(user);

        return ItemMapper.mapItemToDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto modifyItem(Long userId, Long itemId, ItemDto itemDto) {    // изменяем вещь

        Item savedItem = itemRepository.findById(itemId)
                                       .orElseThrow(() -> new DataNotFoundException("Вещь с Id = " + itemId + " не найдена!"));

        if (!userId.equals(savedItem.getOwner().getId())) {
            throw new DataNotFoundException(String.format("Пользователь с Id = %s не является владельцем вещи с Id = %s", userId, itemId));
        }
        Item item = ItemMapper.mapDtoToItem(itemDto);

        if (item.getName() != null) {
            savedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            savedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }
//        if (item.getOwner() != null) {
//            savedItem.setOwner(item.getOwner());
//        }
        if (item.getRequest() != null) {
            savedItem.setRequest(item.getRequest());
        }
        return ItemMapper.mapItemToDto(itemRepository.save(savedItem));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {    // запрос вещи по Id
        LocalDateTime rightNow = LocalDateTime.now();

        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new DataNotFoundException("Вещь с Id = " + itemId + " не найдена!"));

        ItemDto itemDto = ItemMapper.mapItemToDto(item);

        if (userId.equals(item.getOwner().getId())) {   // если вещь запросил хозяин, добавляем даты бронирования
            Booking lastBooking = bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(itemId, rightNow, BookingStatus.APPROVED).orElse(null);

            Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStart(itemId, rightNow, BookingStatus.APPROVED).orElse(null);

            itemDto.setLastBooking(BookingMapper.mapBookingToShortDto(lastBooking));
            itemDto.setNextBooking(BookingMapper.mapBookingToShortDto(nextBooking));
        }
        List<CommentShortDto> commentList = commentRepository.findByItem_Id(itemId).stream()
            .map(CommentMapper::mapCommentToShort)
            .collect(Collectors.toList());

        itemDto.setComments(commentList);
        return itemDto;
    }

    @Override
    public List<ItemDto> findItems(String text) {    // поиск вещи по названию/описанию
        if (text.isBlank()) return new ArrayList<>();   // если строка для поиска пустая, возвращаем пустой список
        return itemRepository.findItemByNameOrDescriptionContainsAllIgnoreCaseAndAvailableIsTrue(text, text).stream()
            .map(ItemMapper::mapItemToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {    // запрос всех вещей пользователя
        LocalDateTime rightNow = LocalDateTime.now();

        User owner = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));

        List<ItemDto> itemDtoList = itemRepository.findItemByOwnerOrderById(owner).stream()
            .map(ItemMapper::mapItemToDto)
            .collect(Collectors.toList());

        for (ItemDto itemDto : itemDtoList) {   // если вещь запросил хозяин, добавляем даты бронирования
            Long itemId = itemDto.getId();

            Booking lastBooking = bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(itemId, rightNow, BookingStatus.APPROVED).orElse(null);

            Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStart(itemId, rightNow, BookingStatus.APPROVED).orElse(null);

            itemDto.setLastBooking(BookingMapper.mapBookingToShortDto(lastBooking));
            itemDto.setNextBooking(BookingMapper.mapBookingToShortDto(nextBooking));

            List<CommentShortDto> commentList = commentRepository.findByItem_Id(itemId).stream()
                    .map(CommentMapper::mapCommentToShort)
                        .collect(Collectors.toList());

            itemDto.setComments(commentList);
        }
        return itemDtoList;
    }

    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        LocalDateTime now = LocalDateTime.now();

        Comment comment = CommentMapper.mapDtoToComment(commentRequestDto);

        comment.setCreated(LocalDateTime.now());
        Booking booking = bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBeforeAndStatusOrderByStartDesc(userId, itemId, now, BookingStatus.APPROVED)
            .orElseThrow(() -> new ValidationException("Вы не бронировали вещь с Id = " + itemId));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new DataNotFoundException("Вещь с Id = " + itemId + " не найдена"));

        comment.setAuthor(user);
        comment.setItem(item);
        return CommentMapper.mapCommentToDto(commentRepository.save(comment));
    }

}
