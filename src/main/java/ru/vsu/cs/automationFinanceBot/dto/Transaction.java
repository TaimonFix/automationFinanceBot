package ru.vsu.cs.automationFinanceBot.dto;

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
    private float sum;

    public Transaction(Long userId) {
        this.userId = userId;
    }
}
