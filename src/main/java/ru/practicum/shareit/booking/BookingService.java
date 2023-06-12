package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingService {

    Booking createBooking(BookingRequestDto bookingRequestDtoDto, Long userId);

    Booking approveBooking(Long bookingId, Long userId, Boolean approved);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking>  getBookingByUser(Long userId, String state);

    List<Booking> getAllBookingOfOwner(Long userId, String state);

//    Booking getLastByUser(User user);
//
//    Booking getNextByUser(User user);
}
