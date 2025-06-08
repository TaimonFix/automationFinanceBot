package ru.vsu.cs.automationFinanceBot.parsers.file;

import ru.vsu.cs.automationFinanceBot.model.dto.CategoryDTO;
import ru.vsu.cs.automationFinanceBot.model.dto.TransactionDTO;

import java.io.File;
import java.util.List;

/**
 * Интерфейс для записи данных в файл
 */
public interface TableFileWriter {
    /**
     * Записать данные в файл.
     *
     * @param transactions список транзакций
     * @param categories список категорий, сформированных из транзакций.
     * @return Файл с данными о транзакциях
     */
    File write(List<TransactionDTO> transactions, List<CategoryDTO> categories);
}
