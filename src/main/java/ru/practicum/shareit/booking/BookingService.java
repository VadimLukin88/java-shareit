package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto bookingRequestDtoDto, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    List<BookingResponseDto>  getBookingByUser(Long userId, BookingState state, int from, int size);

    List<BookingResponseDto> getAllBookingOfOwner(Long userId, BookingState state, int from, int size);

}
