package ru.vsu.cs.automationFinanceBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.vsu.cs.automationFinanceBot.dto.CategoryDTO;
import ru.vsu.cs.automationFinanceBot.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.dto.TransactionDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Поиск транзакций за определенный период
     * @param userId идентификатор пользователя
     * @param dateFrom дата начала периода
     * @param dateTo дата окончания периода
     */
    @Query("SELECT new ru.vsu.cs.automationFinanceBot.dto.TransactionDTO(t.dateTime, t.category, t.description, t.sum) " +
            "FROM Transaction t " +
            "WHERE t.userId = :user_id AND t.dateTime BETWEEN :date_from AND :date_to")
    List<TransactionDTO> findTransactionsBetweenDateFromAndDateTo(@Param("user_id") Long userId,
                                                                  @Param("date_from") LocalDateTime dateFrom,
                                                                  @Param("date_to") LocalDateTime dateTo);

    /**
     * Группировка транзакций по категориям за определенный период
     * @param userId идентификатор пользователя
     * @param dateFrom дата начала периода
     * @param dateTo дата окончания периода
     */
    @Query("SELECT new ru.vsu.cs.automationFinanceBot.dto.CategoryDTO(t.category, SUM(t.sum)) " +
            "FROM Transaction t " +
            "WHERE t.userId = :user_id AND t.dateTime BETWEEN :date_from AND :date_to " +
            "GROUP BY t.category")
    List<CategoryDTO> groupByCategoryBetweenDateFromAndDateTo(@Param("user_id") Long userId,
                                                              @Param("date_from") LocalDateTime dateFrom,
                                                              @Param("date_to") LocalDateTime dateTo);
}
