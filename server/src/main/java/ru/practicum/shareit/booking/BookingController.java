package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.ValidationException;


import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
//@Validated
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
//    @Validated(OnCreate.class)
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("HTTP_POST: Получен запрос на создания бронирования вещи с Id = {} от пользователя Id = {}", bookingRequestDto.getItemId(), userId);
        return bookingService.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        log.info("HTTP_PATCH: Получен запрос на подтверждение бронирования с Id = {} от пользователя Id = {}", bookingId, userId);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long bookingId) {
        log.info("HTTP_GET: Получен запрос на получение бронирования с Id = {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false) String state,
//                                                     @PositiveOrZero(message = "Индекс страницы не может быть отрицательным!")
                                                     @RequestParam(defaultValue = "0", required = false) int from,
//                                                     @Positive(message = "Размер страницы должен быть больше 0!")
                                                     @RequestParam(defaultValue = "5", required = false) int size) {
        log.info("HTTP_GET: Получен запрос на получение всех бронирований пользоваетеля с Id = {}", userId);
        BookingState bookingState;

        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingService.getBookingByUser(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL", required = false) String state,
//                                                         @PositiveOrZero(message = "Индекс страницы не может быть отрицательным!")
                                                         @RequestParam(defaultValue = "0", required = false) int from,
//                                                         @Positive(message = "Размер страницы должен быть больше 0!")
                                                         @RequestParam(defaultValue = "5", required = false) int size) {
        log.info("HTTP_GET: Получен запрос на получение всех бронирований для вещей пользоваетеля с Id = {}", userId);
        BookingState bookingState;

        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingService.getAllBookingOfOwner(userId, bookingState, from, size);
    }

}
