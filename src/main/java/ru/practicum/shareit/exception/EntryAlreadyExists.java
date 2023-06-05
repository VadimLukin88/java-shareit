package ru.practicum.shareit.exception;

public class EntryAlreadyExists extends RuntimeException {

    public EntryAlreadyExists(String message) {
        super(message);
    }
}
