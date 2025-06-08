package ru.vsu.cs.automationFinanceBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.automationFinanceBot.model.dto.AnalysisDTO;
import ru.vsu.cs.automationFinanceBot.model.dto.CategoryDTO;
import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.model.dto.TransactionDTO;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Поиск транзакций за определенный период
     *
     * @param analysis объект, содержащий данные для анализа: id пользователя, дата начала и окончания анализа.
     */
    @Query("SELECT new ru.vsu.cs.automationFinanceBot.model.dto.TransactionDTO(t.dateTime, t.category, t.description, t.sum) " +
            "FROM Transaction t " +
            "WHERE t.userId = :#{#analysis.userId} AND t.dateTime BETWEEN :#{#analysis.dateFrom} AND :#{#analysis.dateTo}")
    List<TransactionDTO> findTransactionsBetweenDateFromAndDateTo(@Param("analysis") AnalysisDTO analysis);

    /**
     * Группировка транзакций по категориям за определенный период
     *
     * @param analysis объект, содержащий данные для анализа: id пользователя, дата начала и окончания анализа.
     */
    @Query("SELECT new ru.vsu.cs.automationFinanceBot.model.dto.CategoryDTO(t.category, SUM(t.sum)) " +
            "FROM Transaction t " +
            "WHERE t.userId = :#{#analysis.userId} AND t.dateTime BETWEEN :#{#analysis.dateFrom} AND :#{#analysis.dateTo} " +
            "GROUP BY t.category")
    List<CategoryDTO> groupByCategoryBetweenDateFromAndDateTo(@Param("analysis") AnalysisDTO analysis);
}
