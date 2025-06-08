package ru.vsu.cs.automationFinanceBot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных для анализа.
 */
@Data
@AllArgsConstructor
public class AnalysisDTO {
    private Long userId;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    public AnalysisDTO(Long userId) {
        this.userId = userId;
    }
}
