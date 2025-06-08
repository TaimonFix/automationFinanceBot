package ru.vsu.cs.automationFinanceBot.parsers.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import ru.vsu.cs.automationFinanceBot.model.dto.CategoryDTO;
import ru.vsu.cs.automationFinanceBot.model.dto.TransactionDTO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Класс для записи данных о транзакциях в Excel файл.
 * Реализация {@link TableFileWriter}.
 */
@Slf4j
public class ExcelTableFileWriter implements TableFileWriter {

    /**
     * Записать данные в файл.
     *
     * @param transactions список транзакций
     * @param categories   список категорий, сформированных из транзакций.
     * @return Файл с данными о транзакциях
     */
    @Override
    public File write(List<TransactionDTO> transactions, List<CategoryDTO> categories) {
        try (Workbook workbook = new XSSFWorkbook()){
            // Лист 1 — Транзакции
            createTransactionsSheet(workbook, transactions);

            // Лист 2 — Категории
            createCategoriesSheet(workbook, categories);

            File file = File.createTempFile("finance_report", ".xlsx");
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }
            return file;
        } catch (IOException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    /**
     * Создать лист с транзакциями
     *
     * @param workbook Файл, в который будут записываться данные
     * @param transactions данные о транзакциях
     */
    private void createTransactionsSheet(Workbook workbook, List<TransactionDTO> transactions) {
        Sheet sheet1 = workbook.createSheet("Транзакции");

        Row header1 = sheet1.createRow(0);
        header1.createCell(0).setCellValue("Дата");
        header1.createCell(1).setCellValue("Категория");
        header1.createCell(2).setCellValue("Описание");
        header1.createCell(3).setCellValue("Сумма");

        int rowNum1 = 1;
        for (TransactionDTO t : transactions) {
            Row row = sheet1.createRow(rowNum1++);
            row.createCell(0).setCellValue(t.getDateTime().toString());
            row.createCell(1).setCellValue(t.getCategory());
            row.createCell(2).setCellValue(t.getDescription());
            row.createCell(3).setCellValue(t.getSum());
        }
        sheet1.autoSizeColumn(0);
        sheet1.autoSizeColumn(1);
        sheet1.autoSizeColumn(2);
        sheet1.autoSizeColumn(3);
    }

    /**
     * Создать лист с категориями
     *
     * @param workbook Файл, в который будут записываться данные
     * @param categories данные о категориях
     */
    private void createCategoriesSheet(Workbook workbook, List<CategoryDTO> categories) {
        Sheet sheet2 = workbook.createSheet("Категории");
        Row header2 = sheet2.createRow(0);
        header2.createCell(0).setCellValue("Категория");
        header2.createCell(1).setCellValue("Сумма");


        int rowNum2 = 1;
        for (CategoryDTO c : categories) {
            Row row = sheet2.createRow(rowNum2++);
            row.createCell(0).setCellValue(c.getCategory());
            row.createCell(1).setCellValue(c.getSum());
        }
        sheet2.autoSizeColumn(0);
        sheet2.autoSizeColumn(1);
        sheet2.autoSizeColumn(2);
        sheet2.autoSizeColumn(3);
        drawPieDiagram(sheet2, categories);
    }

    /**
     * Нарисовать круговую диаграмму в листе с категориями.
     *
     * @param sheet лист, на котором будет рисоваться диаграмма
     * @param categories данные о категориях
     */
    private void drawPieDiagram(Sheet sheet, List<CategoryDTO> categories) {
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, 1, 10, 20);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Категории расходов");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.RIGHT);

        XDDFDataSource<String> categoriesRange = XDDFDataSourcesFactory.fromStringCellRange(
                (XSSFSheet) sheet, new CellRangeAddress(1, categories.size(), 0, 0));
        XDDFNumericalDataSource<Double> valuesRange = XDDFDataSourcesFactory.fromNumericCellRange(
                (XSSFSheet) sheet, new CellRangeAddress(1, categories.size(), 1, 1));

        XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
        data.setVaryColors(true);
        data.addSeries(categoriesRange, valuesRange);
        chart.plot(data);
    }
}
