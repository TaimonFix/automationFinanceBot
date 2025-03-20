package ru.vsu.cs.automationFinanceBot.parsers.file;

import ru.vsu.cs.automationFinanceBot.dto.Transaction;

import java.util.List;

public interface TableFileReader {

    List<Transaction> read(String filepath);
}
