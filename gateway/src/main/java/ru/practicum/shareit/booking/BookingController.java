package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState bookingState;

        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Get booking with state {}, userId={}, from={}, size={}", bookingState, userId, from, size);
        return bookingClient.getBookings(userId, bookingState, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingRequestDto requestDto) {
        if (requestDto.getStart().equals(requestDto.getEnd())) {    // проверка start time = end time
            throw new ValidationException("Время начала аренды равно времени завершения аренды.");
        } else if (requestDto.getStart().isAfter(requestDto.getEnd())) {    // проверка start time > end time
            throw new ValidationException("Время начала аренды позже времени завершения аренды.");
        }
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        log.info("HTTP_PATCH: Получен запрос на подтверждение бронирования с Id = {} от пользователя Id = {}", bookingId, userId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @PositiveOrZero(message = "Индекс страницы не может быть отрицательным!")
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @Positive(message = "Размер страницы должен быть больше 0!")
                                                         @RequestParam(defaultValue = "5") int size) {
        log.info("HTTP_GET: Получен запрос на получение всех бронирований для вещей пользоваетеля с Id = {}", userId);
        BookingState bookingState;

        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingClient.getAllBookingOfOwner(userId, bookingState, from, size);
    }
}
