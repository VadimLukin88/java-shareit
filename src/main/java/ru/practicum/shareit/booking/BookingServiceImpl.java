package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                               UserRepository userRepository,
                               ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        if (bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())) {    // проверка start time = end time
            throw new ValidationException("Время начала аренды равно времени завершения аренды.");
        } else if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {    // проверка start time > end time
            throw new ValidationException("Время начала аренды позже времени завершения аренды.");
        }
        Long itemId = bookingRequestDto.getItemId();

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new DataNotFoundException("Вещь с Id = " + itemId + " не найдена!"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с Id = " + item.getId() + " недоступна для бронирования.");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new DataNotFoundException("Вы не можете забронировать свою вещь.");
        }
        User booker = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));

        Booking newBooking = BookingMapper.mapDtoToBooking(bookingRequestDto, item, booker);

        newBooking.setStatus(BookingStatus.WAITING);
        return BookingMapper.mapBookingToDto(bookingRepository.save(newBooking));
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Long userId, Boolean approved) {
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
        return BookingMapper.mapBookingToDto(bookingRepository.save(savedBooking));
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking savedBooking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new DataNotFoundException("Бронирование с Id = " + bookingId + " не найдено!"));
        // валидация пользователя
        if (!userId.equals(savedBooking.getBooker().getId()) && !userId.equals(savedBooking.getItem().getOwner().getId())) {
            throw new DataNotFoundException("Вы не являетесь инициатором бронирования или владельцем вещи!");
        }
        return BookingMapper.mapBookingToDto(savedBooking);
    }

    @Override
    public List<BookingResponseDto> getBookingByUser(Long userId, BookingState state, int from, int size) {
        LocalDateTime rightNow = LocalDateTime.now();

        Sort sort =  Sort.by("start").descending();

        Pageable pageable = PageRequest.of((from + 1) / size, size, sort);

        User booker = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));

        List<Booking> bookingList;

        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByBooker(booker, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerAndStartBeforeAndEndAfter(booker, rightNow, rightNow, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerAndEndBefore(booker, rightNow, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerAndStartAfter(booker, rightNow, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerAndStatus(booker, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerAndStatus(booker, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookingList = new ArrayList<>();
        }
        return bookingList.stream()
            .map(BookingMapper::mapBookingToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingOfOwner(Long userId, BookingState state, int from, int size) {
        Sort sort =  Sort.by("start").descending();

        Pageable pageable = PageRequest.of(from, size, sort);

        User owner = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Пользователь с Id = " + userId + " не найден!"));

        LocalDateTime rightNow = LocalDateTime.now();

        List<Booking> bookingList;

        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByItem_Owner(owner, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfter(owner, rightNow, rightNow, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findByItem_OwnerAndEndBefore(owner, rightNow, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItem_OwnerAndStartAfter(owner, rightNow, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findByItem_OwnerAndStatus(owner, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItem_OwnerAndStatus(owner, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookingList = new ArrayList<>();
        }
        return bookingList.stream()
            .map(BookingMapper::mapBookingToDto)
            .collect(Collectors.toList());
    }

}
