package ru.vsu.cs.automationFinanceBot.exceptions;

public class UnsopportedFileFormatException extends RuntimeException {
    public UnsopportedFileFormatException(String message) {
        super(message);
    }
}
