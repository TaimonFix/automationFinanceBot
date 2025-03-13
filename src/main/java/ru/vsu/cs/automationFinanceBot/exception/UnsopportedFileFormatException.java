package ru.vsu.cs.automationFinanceBot.exception;

public class UnsopportedFileFormatException extends RuntimeException {
    public UnsopportedFileFormatException(String message) {
        super(message);
    }
}
