package ru.vsu.cs.automationFinanceBot.parsers;

import org.apache.poi.ss.usermodel.*;
import ru.vsu.cs.automationFinanceBot.dto.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SberTableFileReader implements TableFileReader {

    // TODO: Обработчик ошибок в парсере реализовать!!!
    @Override
    public List<Transaction> read(String filepath) {
        try {
            return switch (filepath.split("\\.")[1]) {
                case "xlsx", "xls" -> readExcel(filepath);
                default ->
                        throw new UnsupportedOperationException(
                                "Расширение '" + filepath.split("\\.")[1] + "' не поддерживается.");
            };
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private List<Transaction> readExcel(String filepath) throws DateTimeParseException, IOException {
        List<Transaction> transactions = new LinkedList<>();
        try (FileInputStream fis = new FileInputStream(filepath)) {

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

                    LocalDateTime dateTime = RusDateReader.parse(row.getCell(dateColumn).toString());
                    String category = row.getCell(categoryColumn).toString();
                    float sum = Float.parseFloat(row.getCell(sumColumn).toString());
                    String description = row.getCell(descriptionColumn).toString();
                    transactions.add(new Transaction(dateTime, category, description, sum));
                }
            }


        } catch (Exception e) {
         throw new RuntimeException();
        }
        return transactions;
    }


    public static void main(String[] args) {
        TableFileReader fileReader = new SberTableFileReader();
        List<Transaction> transactions = new ArrayList<>();
         transactions = fileReader.read("excel_1.xlsx");
        System.out.println(transactions);
    }
}
