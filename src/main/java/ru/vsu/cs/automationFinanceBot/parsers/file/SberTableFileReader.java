package ru.vsu.cs.automationFinanceBot.parsers.file;

import org.apache.poi.ss.usermodel.*;
import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.exceptions.UnsupportedFileFormatException;
import ru.vsu.cs.automationFinanceBot.parsers.date.RUSDateReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;


public class SberTableFileReader implements TableFileReader {

    // TODO: Обработчик ошибок в парсере реализовать!!!
    @Override
    public List<Transaction> read(Long userId, String filepath) {
        System.out.println(filepath);
        try {
            String[] args = filepath.split("\\.");
            return switch (args[args.length-1]) {
                case "xlsx", "xls" -> readExcel(userId, filepath);
                default ->
                        throw new UnsupportedFileFormatException(
                                "Расширение '" + args[args.length-1] + "' не поддерживается.");
            };
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Transaction> readExcel(Long userId, String filepath) throws DateTimeParseException, IOException {
        List<Transaction> transactions = new LinkedList<>();
        try (BufferedInputStream fis = new BufferedInputStream(new URL(filepath).openStream())) {

            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int dateColumn = -1;
            int categoryColumn = -1;
            int sumColumn = -1;
            int descriptionColumn = -1;

            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (row.getRowNum() == row.getFirstCellNum()) {
                        switch (cell.toString()) {
                            case "Дата" -> dateColumn = cell.getColumnIndex();
                            case "Категория" -> categoryColumn = cell.getColumnIndex();
                            case "Сумма" -> sumColumn = cell.getColumnIndex();
                            case "Описание" -> descriptionColumn = cell.getColumnIndex();
                        }
                    }
                }
                if (row.getRowNum() != 0 && dateColumn != -1 &&
                        categoryColumn != -1 && sumColumn != -1 && descriptionColumn != -1) {

                    LocalDateTime dateTime = RUSDateReader.parse(row.getCell(dateColumn).toString());
                    String category = row.getCell(categoryColumn).toString();
                    float sum = Float.parseFloat(row.getCell(sumColumn).toString());
                    String description = row.getCell(descriptionColumn).toString();
                    transactions.add(new Transaction(userId, dateTime, category, description, sum));
                }
            }
        }
        return transactions;
    }
}
