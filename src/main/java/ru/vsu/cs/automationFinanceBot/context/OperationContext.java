package ru.vsu.cs.automationFinanceBot.context;

import org.springframework.stereotype.Component;
import ru.vsu.cs.automationFinanceBot.model.enums.Operation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс, хранящий операцию, на которой находится пользователь.
 */
@Component
public class OperationContext {
    private final Map<Long, Operation> operations = new ConcurrentHashMap<>();

    /**
     * Получение операции
     *
     * @param userId id пользователя
     * @return {@link Operation} операция, на которой находится пользователь.
     */
    public Operation getOperation(Long userId) {
        return operations.getOrDefault(userId, Operation.NONE);
    }

    /**
     * Назначить операцию
     *
     * @param userId id пользователя
     * @param operation {@link Operation} операция.
     */
    public void setOperation(Long userId, Operation operation) {
        operations.put(userId, operation);
    }
}
