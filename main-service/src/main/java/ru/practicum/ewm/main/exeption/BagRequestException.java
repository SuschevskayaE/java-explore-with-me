package ru.practicum.ewm.main.exeption;

public class BagRequestException extends RuntimeException {
    public BagRequestException(final String message) {
        super(message);
    }
}
