package ru.vsu.cs.automationFinanceBot.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private LocalDateTime dateTime;
    @NotNull
    private String category;
    private String description;
    @NotNull
    private float sum;

    public Transaction(Long userId) {
        this.userId = userId;
    }

    public Transaction(LocalDateTime dateTime, String category, String description, float sum) {
        this.dateTime = dateTime;
        this.category = category;
        this.description = description;
        this.sum = sum;
    }
}
