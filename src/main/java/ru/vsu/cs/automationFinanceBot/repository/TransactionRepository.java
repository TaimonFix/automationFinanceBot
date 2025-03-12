package ru.vsu.cs.automationFinanceBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.automationFinanceBot.dto.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
