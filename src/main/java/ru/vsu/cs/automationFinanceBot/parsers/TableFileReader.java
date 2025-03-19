package ru.vsu.cs.automationFinanceBot.parsers;

import ru.vsu.cs.automationFinanceBot.dto.Transaction;

import java.util.List;

public interface TableFileReader {

    List<Transaction> read(String filepath);
}
