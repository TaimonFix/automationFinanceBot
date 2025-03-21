package ru.vsu.cs.automationFinanceBot.dto;

import java.time.LocalDateTime;

/**
 * DTO для хранения транзакций за определенный период
 */

public class TransactionDTO {
    private LocalDateTime dateTime;
    private String category;
    private String description;
    private Float sum;

    public TransactionDTO(LocalDateTime dateTime, String category, String description, Float sum) {
        this.dateTime = dateTime;
        this.category = category;
        this.description = description;
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "dateTime=" + dateTime +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", sum=" + sum +
                '}';
    }
}
