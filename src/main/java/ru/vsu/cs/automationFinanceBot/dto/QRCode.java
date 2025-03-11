package ru.vsu.cs.automationFinanceBot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QRCode {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private LocalDateTime dateTime;
    private float sum;
    private long fn;
    private int i;
    private int fp;
    private int n;

}
