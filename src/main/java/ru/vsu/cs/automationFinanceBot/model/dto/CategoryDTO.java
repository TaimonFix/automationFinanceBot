package ru.vsu.cs.automationFinanceBot.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для разбиения транзакций по категориям за определенный период
 */

@Data
@AllArgsConstructor
public class CategoryDTO {
    private String category;
    private Double sum;
}
