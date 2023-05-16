package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Long item;
    private Long booker;
    private BookingStatus status;
}
