package ru.vsu.cs.automationFinanceBot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vsu.cs.automationFinanceBot.enums.Command;
import ru.vsu.cs.automationFinanceBot.enums.Operation;
import ru.vsu.cs.automationFinanceBot.dto.QRCode;
import ru.vsu.cs.automationFinanceBot.dto.Transaction;
import ru.vsu.cs.automationFinanceBot.exceptions.QRCodeRecognizeException;
import ru.vsu.cs.automationFinanceBot.parsers.QRCodeReader;
import ru.vsu.cs.automationFinanceBot.services.TransactionService;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final BotToken botToken;
    private final TransactionService transactionService;

    private final String PREFIX_COMMAND = "/";

    @Value("${bot.name}")
    private String botName;

    private InlineKeyboardMarkup keyboardMarkup;

    private InlineKeyboardButton button1;
    private InlineKeyboardButton button2;
    private InlineKeyboardButton button3;
    private InlineKeyboardButton button4;
    private Transaction transaction;
    private Operation operation;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                textHandler(update);
            } else if (update.getMessage().hasPhoto()) {
                photoHandler(update);
            }
        } else if (update.hasCallbackQuery()) {
            callbackQueryHandler(update);
        }
    }

    /**
     * Обработчик поступивших текстовых сообщений от пользователе
     * @param update
     */
    private void textHandler(Update update) {
        var message = update.getMessage();
        var user = message.getFrom();
        System.out.println(user.getFirstName() + " wrote " + message.getText());
        System.out.println(user.getId());

        if (message.getText().startsWith(PREFIX_COMMAND)) {
            operationHandler(update);
        } else switch (operation) {
            case Operation.QR_PHOTO -> {
                sendMessage(user.getId(), "Некорректный ввод");
                qr(user.getId());
            }
            case Operation.QR_CATEGORY -> {
                transaction.setCategory(update.getMessage().getText());
                sendMessage(user.getId(), "Введи описание:");
                operation = Operation.QR_DESCRIPTION;
            }
            case QR_DESCRIPTION -> {
                transaction.setDescription(update.getMessage().getText());
                System.out.println(transaction);
                transactionService.addTransaction(transaction);
                // TODO: Сделать обработку ошибок на случай, если трата не сохранится
                sendMessage(user.getId(), "Трата сохранена.");
                // TODO: Вывести юзеру информацию о поступившей трате
                sendMainMenu(user.getId());
            }
            case null, default -> {
                sendMessage(user.getId(), "Некорректный ввод.");
                sendMainMenu(user.getId());
            }

        }
    }

    /**
     * Обработчик поступивших команд
     */
    private void operationHandler(Update update) {
        var message = update.getMessage();
        var user = message.getFrom();

        // TODO: отрефакторить обработку команд
        if (message.getText().startsWith(Command.START.getCommandName())) {
            String text = "Привет! Я - финансовый помощник.\n\nЯ помогу тебе вести бюджет и оптимизировать твои траты." +
                    "\nДавай начнем с ввода данных. Я умею обрабатывать информацию следующими способами:\n" +
                    "1. Ты можешь отправить изображение с чеком, содержащим QR код - я его разберу и внесу " +
                    "траты в свой дневник.\n" +
                    "2. Ты можешь прислать .xlsx/.csv файл с выгрузкой расходов за определенный период из банка " +
                    "(СБЕР, Т-Банк, Газпромбанк) - я проанализирую содержимое и внесу траты в свой дневник.\n" +
                    "3. Ты можешь вручную вписать соответствующую трату.";
            sendMessage(user.getId(), text);
            sendInputDataMenu(user.getId());
        } else if (message.getText().startsWith(Command.MENU.getCommandName())) {
            sendMessage(user.getId(), "Высылаю меню...");
            sendMainMenu(user.getId());

        }
    }

    /**
     * Обработчик поступивших изображений
     */
    private void photoHandler(Update update) {
        if (operation == Operation.QR_PHOTO) {
            try {
                // TODO: заняться парсингом данных с QR кода с последующей отправкой в БД
                operation = Operation.QR_CATEGORY;
                QRCode qrCode = QRCodeReader.parse(inputQRPhoto(update.getMessage().getPhoto()));
                transaction.setSum(qrCode.getSum());
                transaction.setDateTime(qrCode.getDateTime());
                sendMessage(update.getMessage().getFrom().getId(), "Введи категорию");

            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            } catch (QRCodeRecognizeException e) {
                sendMessage(update.getMessage().getFrom().getId(), e.getMessage());
            }
        }
    }

    /**
     * Обработчик нажатой кнопки
     */
    private void callbackQueryHandler(Update update) {
        var callbackData = update.getCallbackQuery().getData();
        var userId = update.getCallbackQuery().getFrom().getId();
        switch (callbackData) {
            case "qr" -> qr(userId);
            case "fileFromBank" -> fileFromBank();
            case "manualInput" -> manualInput();
            case "mainMenu" -> sendMainMenu(userId);
            case "inputData" -> sendInputDataMenu(userId);
            case "analysis" -> sendAnalysisDataMenu(userId);
        }
    }
    /**
     * Отправка сообщения пользователю
     *
     * @param who id пользователя
     * @param txt сообщение
     */
    private void sendMessage(Long who, String txt) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) // кому мы отправляем сообщение
                .text(txt).build(); // содержимое сообщения

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Выводит меню с выбором варианта ответа
     *
     * @param who id пользователя
     * @param txt текст меню
     * @param kb  Экземпляр класса InlineKeyboardMarkup, содержащий необходимые кнопки
     */
    private void sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Главное меню
     */
    private void sendMainMenu(Long id) {
        transaction = new Transaction(id);
        operation = Operation.MAIN_MENU;
        button1 = InlineKeyboardButton.builder()
                .text("Ввод данных")
                .callbackData("inputData")
                .build();
        button2 = InlineKeyboardButton.builder()
                .text("Анализ расходов")
                .callbackData("analysis")
                .build();
        keyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button1))
                .keyboardRow(List.of(button2))
                .build();
        sendMenu(id, "Выбери на клавиатуре соответствующее действие:", keyboardMarkup);
    }

    /**
     * Создание меню выбора ввода данных
     * 1. QR-код
     * 2. .xlsx/.csv выгрузка из банка
     * 3. ручной ввод
     * 4. Выход в главное меню
     */
    private void sendInputDataMenu(Long id) {
        transaction = new Transaction(id);
        operation = Operation.INPUT_DATA_MENU;
        button1 = InlineKeyboardButton.builder()
                .text("QR-код")
                .callbackData("qr")
                .build();
        button2 = InlineKeyboardButton.builder()
                .text("Выгрузка из банка")
                .callbackData("fileFromBank")
                .build();
        button3 = InlineKeyboardButton.builder()
                .text("Вручную")
                .callbackData("manualInput")
                .build();
        button4 = InlineKeyboardButton.builder()
                .text("Выход в главное меню")
                .callbackData("mainMenu")
                .build();
        keyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button1))
                .keyboardRow(List.of(button2))
                .keyboardRow(List.of(button3))
                .keyboardRow(List.of(button4))
                .build();
        sendMenu(id, "Выбери на клавиатуре соответствующее действие:", keyboardMarkup);
    }

    private void sendAnalysisDataMenu(Long id) {
        operation = Operation.ANALYSIS_DATA_MENU;
        sendMessage(id, "Выберите период для анализа.");
    }

    private void qr(Long id) {
        operation = Operation.QR_PHOTO;
        sendMessage(id, "Пришли фото чека, содержащего QR-код");
    }

    private String inputQRPhoto(List<PhotoSize> photos) throws QRCodeRecognizeException,
            IOException, TelegramApiException {
        PhotoSize photo = photos.getLast();
        GetFile getFile = new GetFile();
        getFile.setFileId(photo.getFileId());
        File file = execute(getFile);
        String photoPath = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
        return QRCodeReader.decodeQR(QRCodeReader.downloadPhoto(photoPath));
    }

    private void fileFromBank() {
        System.out.println("fileFromBank");
    }

    private void manualInput() {
        System.out.println("manualInput");
    }
}
