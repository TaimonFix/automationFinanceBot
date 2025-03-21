package ru.vsu.cs.automationFinanceBot.exceptions;

public class UnsupportedFileFormatException extends RuntimeException {
    public UnsupportedFileFormatException(String message) {
        super(message);
    }
}
