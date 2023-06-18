package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Component
public class BookingMapper {

    public static Booking mapDtoToBooking(BookingRequestDto bookingDto) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd());
    }

    public static BookingResponseDto mapBookingToDto(Booking booking) {
        return new BookingResponseDto(booking.getId(),
                              booking.getStart(),
                              booking.getEnd(),
                              ItemMapper.mapItemToShortDto(booking.getItem()),
                              UserMapper.mapUserToShortDto(booking.getBooker()),
                              booking.getStatus());
    }

    public static BookingShortDto mapBookingToShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingShortDto(booking.getId(),
                                   booking.getBooker().getId());
    }

}
