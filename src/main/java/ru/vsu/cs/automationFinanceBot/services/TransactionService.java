package ru.vsu.cs.automationFinanceBot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.automationFinanceBot.dto.CategoryDTO;
import ru.vsu.cs.automationFinanceBot.dto.TransactionDTO;
import ru.vsu.cs.automationFinanceBot.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Добавление одной транзакции
     */
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Добавление списка транзакций
     */
    // TODO: Обработать случай, чтобы повторные операции не сохранялись
    public List<Transaction> addTransactions(List<Transaction> transactions) {
        return transactionRepository.saveAll(transactions);
    }

    /**
     * Поиск транзакций за определенный период
     * @param userId идентификатор пользователя
     * @param dateFrom дата начала периода
     * @param dateTo дата окончания периода
     */
    public List<TransactionDTO> findTransactions(Long userId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return transactionRepository.findTransactionsBetweenDateFromAndDateTo(userId, dateFrom, dateTo);
    }

    /**
     * Группировка транзакций по категориям за определенный период
     * @param userId идентификатор пользователя
     * @param dateFrom дата начала периода
     * @param dateTo дата окончания периода
     */
    public List<CategoryDTO> findTransactionsGroupByCategories(Long userId,
                                                               LocalDateTime dateFrom,
                                                               LocalDateTime dateTo) {
        return transactionRepository.groupByCategoryBetweenDateFromAndDateTo(userId, dateFrom, dateTo);
    }

}
