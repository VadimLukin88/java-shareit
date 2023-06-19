package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapDtoToBooking(BookingRequestDto bookingDto, Item item, User booker) {
        return new Booking(null,
                           bookingDto.getStart(),
                           bookingDto.getEnd(),
                           item,
                           booker,
                        null);
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
