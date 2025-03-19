package ru.vsu.cs.automationFinanceBot.parsers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class RusDateReader {
    private static final Map<String, String> MONTHS_MAP = new HashMap<>();

    static {
        MONTHS_MAP.put("янв", "01");
        MONTHS_MAP.put("фев", "02");
        MONTHS_MAP.put("мар", "03");
        MONTHS_MAP.put("апр", "04");
        MONTHS_MAP.put("май", "05");
        MONTHS_MAP.put("июн", "06");
        MONTHS_MAP.put("июл", "07");
        MONTHS_MAP.put("авг", "08");
        MONTHS_MAP.put("сен", "09");
        MONTHS_MAP.put("окт", "10");
        MONTHS_MAP.put("ноя", "11");
        MONTHS_MAP.put("дек", "12");
    }

    public static LocalDateTime parse(String inputDate) throws DateTimeParseException {
        inputDate = replacesDots(inputDate);
        for (Map.Entry<String, String> entry : MONTHS_MAP.entrySet()) {
            if (inputDate.contains(entry.getKey())) {
                inputDate = inputDate.replace(entry.getKey(), entry.getValue()); // Заменяем месяц на число
                break;
            }
        }

        // Теперь строка в формате "02 02 2025, 17:52"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy, HH:mm");

        return LocalDateTime.parse(inputDate, formatter);
    }

    private static String replacesDots(String string) {
        return string.replaceFirst("\\.", "").toLowerCase();
    }

    public static void main(String[] args) {
        String date = "02 янв. 2025, 17:52";

        LocalDateTime dateTime = parse(date);

        System.out.println("Преобразованная дата: " + dateTime);
    }
}
