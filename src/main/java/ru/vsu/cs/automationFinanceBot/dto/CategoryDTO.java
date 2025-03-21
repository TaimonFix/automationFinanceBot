package ru.vsu.cs.automationFinanceBot.dto;


/**
 * DTO для разбиения транзакций по категориям за определенный период
 */

public class CategoryDTO {
    private String category;
    private Double sum;

    public CategoryDTO(String category, Double sum) {
        this.category = category;
        this.sum = sum;
    }

    public CategoryDTO() {
    }

    @Override
    public String toString() {
        return "CategoryDTO{" +
                "category='" + category + '\'' +
                ", sum=" + sum +
                '}';
    }

}
