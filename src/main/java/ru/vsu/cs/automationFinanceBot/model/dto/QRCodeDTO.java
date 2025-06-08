package ru.vsu.cs.automationFinanceBot.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO для хранения данных с QR-кода.
 */
@Data
public class QRCodeDTO {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private LocalDateTime dateTime;
    private float sum;
    private long fn;
    private int i;
    private long fp;
    private int n;

}
