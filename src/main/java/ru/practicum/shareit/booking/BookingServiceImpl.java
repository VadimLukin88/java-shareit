package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;  // подключаем сервисы, чтобы не прописывать повторно исключения для некорректных Id
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl (BookingRepository bookingRepository,
                               UserService userService,
                               ItemService itemService){
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    @Transactional
    public Booking createBooking (BookingRequestDto bookingRequestDto, Long userId) {
        Booking newBooking = BookingMapper.mapDtoToBooking(bookingRequestDto);

        if (newBooking.getStart().equals(newBooking.getEnd())) {    // проверка start time = end time
            throw new ValidationException("Время начала аренды равно времени завершения аренды.");
        } else if (newBooking.getStart().isAfter(newBooking.getEnd())) {    // проверка start time > end time
            throw new ValidationException("Время начала аренды позже времени завершения аренды.");
        }
        Item item = itemService.getItem(userId, bookingRequestDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с Id = " + item.getId() + " недоступна для бронирования.");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new DataNotFoundException("Вы не можете забронировать свою вещь.");
        }

        User user = userService.getUserById(userId);

        newBooking.setItem(item);
        newBooking.setBooker(user);
        newBooking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(newBooking);
    }

    @Override
    @Transactional
    public Booking approveBooking(Long bookingId, Long userId, Boolean approved) {
        Booking savedBooking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new DataNotFoundException("Бронирование с Id = " + bookingId + " не найдено!"));

        if (!userId.equals(savedBooking.getItem().getOwner().getId())) { //валидация пользователя
            throw new DataNotFoundException("Вы не являетесь владельцем вещи с Id = " + savedBooking.getItem().getId());
        }
        if (!savedBooking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Нельзя изменить статус бронирования: текущий статус != WAITING.");
        }
        if (approved) {
            savedBooking.setStatus(BookingStatus.APPROVED);
        } else {
            savedBooking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(savedBooking);
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking savedBooking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new DataNotFoundException("Бронирование с Id = " + bookingId + " не найдено!"));
        // валидация пользователя
        if (!userId.equals(savedBooking.getBooker().getId()) && !userId.equals(savedBooking.getItem().getOwner().getId())) {
            throw new DataNotFoundException("Вы не являетесь инициатором бронирования или владельцем вещи!");
        }
        return savedBooking;
    }

    @Override
    public List<Booking> getBookingByUser(Long userId, String state) {
        User booker = userService.getUserById(userId);

        LocalDateTime rightNow = LocalDateTime.now();

        List<Booking> bookingList;

        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findByBookerOrderByStartDesc(booker);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(booker, rightNow, rightNow);
                break;
            case "PAST":
                bookingList = bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(booker, rightNow);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findByBookerAndStartAfterOrderByStartDesc(booker, rightNow);
                break;
            case "WAITING":
                bookingList = bookingRepository.findByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList;
    }

    @Override
    public List<Booking> getAllBookingOfOwner(Long userId, String state) {
        User owner = userService.getUserById(userId);

        LocalDateTime rightNow = LocalDateTime.now();

        List<Booking> bookingList;

        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findByItem_OwnerOrderByStartDesc(owner);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findByItem_OwnerAndStartAfterAndEndBeforeOrderByStartDesc(owner, rightNow, rightNow);
                break;
            case "PAST":
                bookingList = bookingRepository.findByItem_OwnerAndEndBeforeOrderByStartDesc(owner, rightNow);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findByItem_OwnerAndStartAfterOrderByStartDesc(owner, rightNow);
                break;
            case "WAITING":
                bookingList = bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(owner, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(owner, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList;
    }
//    @Override
//    public Booking getLastByUser(User user) {
//        return bookingRepository.findFirstByItem_OwnerAndStartBeforeOrderByStartDesc(user, LocalDateTime.now());
//    }
//
//    @Override
//    public Booking getNextByUser(User user) {
//        return bookingRepository.findFirstByItem_OwnerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
//    }
}
