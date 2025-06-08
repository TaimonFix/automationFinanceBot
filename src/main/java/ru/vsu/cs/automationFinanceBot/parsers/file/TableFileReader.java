package ru.vsu.cs.automationFinanceBot.parsers.file;

import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;

import java.util.List;

/**
 * Интерфейс для чтения файлов
 */
public interface TableFileReader {

    /**
     * Прочитать файл
     *
     * @param userId id пользователя
     * @param filepath путь, по которому расположен файл.
     * @return
     */
    List<Transaction> read(Long userId, String filepath);
}
