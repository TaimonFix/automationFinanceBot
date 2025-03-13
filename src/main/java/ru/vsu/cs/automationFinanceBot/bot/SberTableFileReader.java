package ru.vsu.cs.automationFinanceBot.bot;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import ru.vsu.cs.automationFinanceBot.dto.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class SberTableFileReader implements TableFileReader {

    @Override
    public List<Transaction> read(String filepath) {
        try {
            switch (filepath.split("\\.")[1]) {
                case "xlsx":
                case "xls":
                    readExcel(filepath);
                    break;
                default:
                    throw new UnsupportedOperationException("Расширение '" + filepath.split("\\.")[1] + "' не поддерживается.");
            }
            return List.of();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private List<Transaction> readExcel(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            List<Transaction> transactions = new LinkedList<>();
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int dateColumn = -1;
            int categoryColumn = -1;
            int sumColumn = -1;
            int descriptionColumn = -1;

            for (Row row: sheet) {
                for (Cell cell: row) {
                    if (row.getRowNum() == row.getFirstCellNum()) {
                        switch (cell.toString()) {
                            case "Дата" -> dateColumn = cell.getColumnIndex();
                            case "Категория" -> categoryColumn = cell.getColumnIndex();
                            case "Сумма" -> sumColumn = cell.getColumnIndex();
                            case "Описание" -> descriptionColumn = cell.getColumnIndex();
                        }
                    }
                }
            if (row.getRowNum() != row.getFirstCellNum()) {
                if (dateColumn != -1 && categoryColumn != -1 && sumColumn != -1 && descriptionColumn != -1) {
                    // TODO: Разобраться с ошибкой, почему не парсится дата
                    LocalDateTime dateTime = LocalDateTime.parse(row.getCell(dateColumn).toString(), Transaction.FORMATTER);
                    String category = row.getCell(categoryColumn).toString();
                    float sum = Float.parseFloat(row.getCell(sumColumn).toString());
                    String description = row.getCell(descriptionColumn).toString();
                    transactions.add(new Transaction(dateTime, category, description, sum));
                }
            }
            }
            return transactions;
        }
    }

    public static void main(String[] args) {
//        TableFileReader fileReader = new SberTableFileReader();
//        System.out.println(fileReader.read("excel_1.xlsx"));

        String date = "02 фев 2025, 17:52";

        System.out.println(LocalDateTime.parse(date, Transaction.FORMATTER));
    }
}
