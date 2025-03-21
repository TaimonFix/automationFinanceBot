package ru.vsu.cs.automationFinanceBot.parsers.file;

import ru.vsu.cs.automationFinanceBot.entities.Transaction;

import java.util.List;

public interface TableFileReader {

    List<Transaction> read(Long userId, String filepath);
}
