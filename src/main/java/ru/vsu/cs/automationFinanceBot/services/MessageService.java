package ru.vsu.cs.automationFinanceBot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vsu.cs.automationFinanceBot.context.OperationContext;
import ru.vsu.cs.automationFinanceBot.model.enums.Operation;

import java.io.File;
import java.util.List;

/**
 * Сервис отправки сообщений.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final OperationContext operationContext;

    /**
     * Отправка сообщения пользователю
     *
     * @param who id пользователя
     * @param txt сообщение
     * @param sender кто будет отправлять сообщение
     */
    public void sendMessage(Long who, String txt, AbsSender sender) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) // кому мы отправляем сообщение
                .text(txt).build(); // содержимое сообщения
        try {
            sender.execute(sm);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Отправка файла пользователю.
     *
     * @param who id пользователя
     * @param file файл
     * @param sender кто будет отправлять сообщение
     */
    public void sendFile(Long who, File file, AbsSender sender) {
        SendDocument document = SendDocument.builder()
                .chatId(who.toString())
                .document(new InputFile(file))
                .caption("Отчет по расходам.")
                .build();

        try {
            sender.execute(document);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Вывод меню с выбором варианта ответа
     *
     * @param who id пользователя
     * @param txt текст меню
     * @param kb  Экземпляр класса InlineKeyboardMarkup, содержащий необходимые кнопки
     * @param sender кто будет отправлять сообщение
     */
    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb, AbsSender sender) {
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            sender.execute(sm);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Вывод главного меню
     *
     * @param id юзера
     * @param sender кто будет отправлять сообщение
     */
    public void sendMainMenu(Long id, AbsSender sender) {
        operationContext.setOperation(id, Operation.MAIN_MENU);
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("Ввод данных")
                .callbackData("inputData")
                .build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                .text("Анализ расходов")
                .callbackData("analysis")
                .build();
        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button1))
                .keyboardRow(List.of(button2))
                .build();
        sendMenu(id, "Выбери на клавиатуре соответствующее действие:", keyboardMarkup, sender);
    }

    /**
     * Создание меню выбора ввода данных
     * 1. QR-код
     * 2. .xlsx/.csv выгрузка из банка
     * 3. ручной ввод
     * 4. Выход в главное меню
     *
     * @param id юзера
     * @param sender кто будет отправлять сообщение
     */
    public void sendInputDataMenu(Long id, AbsSender sender) {
        operationContext.setOperation(id, Operation.INPUT_DATA_MENU);
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("QR-код")
                .callbackData("qr")
                .build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                .text("Выгрузка из банка")
                .callbackData("fileFromBank")
                .build();
        InlineKeyboardButton button3 = InlineKeyboardButton.builder()
                .text("Вручную")
                .callbackData("manualInput")
                .build();
        InlineKeyboardButton button4 = InlineKeyboardButton.builder()
                .text("Выход в главное меню")
                .callbackData("mainMenu")
                .build();
        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button1))
                .keyboardRow(List.of(button2))
                .keyboardRow(List.of(button3))
                .keyboardRow(List.of(button4))
                .build();
        sendMenu(id, "Выбери на клавиатуре соответствующее действие:", keyboardMarkup, sender);
    }

    /**
     * Создание меню с аналитикой
     *
     * @param id юзера
     * @param sender кто будет отправлять сообщение
     */
    public void sendAnalysisDataMenu(Long id, AbsSender sender) {
        operationContext.setOperation(id, Operation.ANALYSIS_DATA_MENU);
        sendMessage(id, "Выберите период для анализа.\n\nВведите дату начала анализа:", sender);
        operationContext.setOperation(id, Operation.INPUT_DATE_START);
    }
}
