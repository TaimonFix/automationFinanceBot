package ru.vsu.cs.automationFinanceBot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для хранения транзакции.
 */
@Data
@AllArgsConstructor
public class TransactionDTO {
    private LocalDateTime dateTime;
    private String category;
    private String description;
    private Float sum;
}
