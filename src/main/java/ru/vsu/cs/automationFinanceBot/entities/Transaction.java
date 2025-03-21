package ru.vsu.cs.automationFinanceBot.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

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
    private Float sum;

    public Transaction(Long userId) {
        this.userId = userId;
    }

    public Transaction(Long userId, LocalDateTime dateTime, String category, String description, Float sum) {
        this.userId = userId;
        this.dateTime = dateTime;
        this.category = category;
        this.description = description;
        this.sum = sum;
    }
}
