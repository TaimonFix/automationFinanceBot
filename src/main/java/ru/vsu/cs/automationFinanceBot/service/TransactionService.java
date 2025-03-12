package ru.vsu.cs.automationFinanceBot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.automationFinanceBot.dto.Transaction;
import ru.vsu.cs.automationFinanceBot.repository.TransactionRepository;

@Service
@RequiredArgsConstructor

public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
