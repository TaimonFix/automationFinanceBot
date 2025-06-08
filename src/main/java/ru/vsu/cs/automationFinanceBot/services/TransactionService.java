package ru.vsu.cs.automationFinanceBot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.automationFinanceBot.model.dto.AnalysisDTO;
import ru.vsu.cs.automationFinanceBot.model.dto.CategoryDTO;
import ru.vsu.cs.automationFinanceBot.model.dto.TransactionDTO;
import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.repositories.TransactionRepository;
import java.util.List;

@Service
@RequiredArgsConstructor

/**
 * Сервис для работы с транзакциями.
 */
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
     *
     * @param analysis объект, содержащий данные для анализа: id пользователя, дата начала и окончания анализа.
     */
    public List<TransactionDTO> findTransactionsBetweenDateFromAndDateTo(AnalysisDTO analysis) {
        return transactionRepository.findTransactionsBetweenDateFromAndDateTo(analysis);
    }

    /**
     * Группировка транзакций по категориям за определенный период
     *
     * @param analysis объект, содержащий данные для анализа: id пользователя, дата начала и окончания анализа.
     */
    public List<CategoryDTO> findTransactionsGroupByCategories(AnalysisDTO analysis) {
        return transactionRepository.groupByCategoryBetweenDateFromAndDateTo(analysis);
    }

}
