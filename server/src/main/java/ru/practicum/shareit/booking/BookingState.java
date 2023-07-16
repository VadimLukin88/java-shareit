package ru.practicum.shareit.booking;

// пользовательские фильтры для отображения бронирований
public enum BookingState {
    ALL,        // все бронирования
    CURRENT,    // текущие
    PAST,       // завершённые
    FUTURE,     // запланированные и подтверждённые
    WAITING,    // запланированные, ожидающие подтверждения
    REJECTED    // отклонённые владельцем
}
