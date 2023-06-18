package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto bookingRequestDtoDto, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    List<BookingResponseDto>  getBookingByUser(Long userId, String state);

    List<BookingResponseDto> getAllBookingOfOwner(Long userId, String state);

}
