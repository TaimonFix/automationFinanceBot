package ru.vsu.cs.automationFinanceBot.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "transaction")
public class Transaction {
    @Id
    private Long id;

    private LocalDateTime dateTime;
    private String category;
    private String description;
    private float sum;
}
