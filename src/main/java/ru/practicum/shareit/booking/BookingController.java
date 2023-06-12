package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.user.dto.OnCreate;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Validated(OnCreate.class)
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid  @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("HTTP_POST: Получен запрос на создания бронирования вещи с Id = {} от пользователя Id = {}", bookingRequestDto.getItemId(), userId);
        return BookingMapper.mapBookingToDto(bookingService.createBooking(bookingRequestDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam Boolean approved) {
        log.info("HTTP_PATCH: Получен запрос на подтверждение бронирования с Id = {} от пользователя Id = {}", bookingId, userId);
        return  BookingMapper.mapBookingToDto(bookingService.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("HTTP_GET: Получен запрос на получение бронирования с Id = {}", bookingId);
        return BookingMapper.mapBookingToDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("HTTP_GET: Получен запрос на получение всех бронирований пользоваетеля с Id = {}", userId);
        return bookingService.getBookingByUser(userId, state).stream()
            .map(BookingMapper::mapBookingToDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("HTTP_GET: Получен запрос на получение всех бронирований для вещей пользоваетеля с Id = {}", userId);
        return bookingService.getAllBookingOfOwner(userId, state).stream()
            .map(BookingMapper::mapBookingToDto)
            .collect(Collectors.toList());
    }

}
