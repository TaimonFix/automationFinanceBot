package ru.vsu.cs.automationFinanceBot.context;

import org.springframework.stereotype.Component;
import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс, хранящий транзакцию, на которой находится пользователь.
 */
@Component
public class TransactionContext {
    private final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();

    /**
     * Получение транзакции
     *
     * @param userId id пользователя
     * @return {@link Transaction} транзакция
     */
    public Transaction getTransaction(Long userId) {
        return transactions.getOrDefault(userId, new Transaction());
    }

    /**
     * Обновить транзакцию
     *
     * @param userId id пользователя
     * @param {@link Transaction} транзакция
     */
    public void setOperation(Long userId, Transaction transaction) {
        transactions.put(userId, transaction);
    }
}
