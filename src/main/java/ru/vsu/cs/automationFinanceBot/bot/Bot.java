package ru.vsu.cs.automationFinanceBot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vsu.cs.automationFinanceBot.enums.Command;
import ru.vsu.cs.automationFinanceBot.enums.Operation;
import ru.vsu.cs.automationFinanceBot.dto.QRCodeDTO;
import ru.vsu.cs.automationFinanceBot.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.exceptions.QRCodeRecognizeException;
import ru.vsu.cs.automationFinanceBot.parsers.file.QRCodeReader;
import ru.vsu.cs.automationFinanceBot.parsers.file.SberTableFileReader;
import ru.vsu.cs.automationFinanceBot.parsers.file.TableFileReader;
import ru.vsu.cs.automationFinanceBot.services.TransactionService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
            } else if (update.getMessage().hasDocument()) {
                documentHandler(update);
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
            case QR_PHOTO -> {
                sendMessage(user.getId(), "Некорректный ввод");
                qr(user.getId());
            }
            case INPUT_DATE -> {
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                try {
                    transaction = new Transaction(user.getId());
                    transaction.setDateTime(LocalDateTime.parse(update.getMessage().getText(),dateFormat));
                    sendMessage(user.getId(), "Введи сумму расхода:");
                    operation = Operation.INPUT_SUM;
                } catch (DateTimeParseException e) {
                    System.out.println(e.getMessage());
                    sendMessage(user.getId(), "Неверный формат даты. Введите в формате: '01.01.2020 18:53'");
                }
            }
            case INPUT_SUM -> {
                try {
                    transaction.setSum(Float.parseFloat(update.getMessage().getText()));
                    sendMessage(user.getId(), "Введи категорию расхода:");
                    operation = Operation.INPUT_CATEGORY;

                } catch (NumberFormatException e) {
                    sendMessage(user.getId(),"Неверный формат числа. Введите в одном из форматах: '255', '255.99'");
                }
            }
            case INPUT_CATEGORY -> {
                transaction.setCategory(update.getMessage().getText());
                sendMessage(user.getId(), "Введи описание:");
                operation = Operation.INPUT_DESCRIPTION;
            }
            case INPUT_DESCRIPTION -> {
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
            String text = """
                    Привет! Я - финансовый помощник.
                    
                    Я помогу тебе вести бюджет и оптимизировать твои траты.\
                    
                    Давай начнем с ввода данных. Я умею обрабатывать информацию следующими способами:
                    1. Ты можешь отправить изображение с чеком, содержащим QR код - я его разберу и внесу \
                    траты в свой дневник.
                    2. Ты можешь прислать .xlsx/.csv файл с выгрузкой расходов за определенный период из банка \
                    (СБЕР, Т-Банк, Газпромбанк) - я проанализирую содержимое и внесу траты в свой дневник.
                    3. Ты можешь вручную вписать соответствующую трату.""";
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
                operation = Operation.INPUT_CATEGORY;
                // TODO: Добавить возможность присылать PDF файл, содержащий QR код
                // TODO: Добавить обработку нескольких изображений
                QRCodeDTO qrCodeDTO = QRCodeReader.parse(inputQRPhoto(update.getMessage().getPhoto()));
                transaction.setSum(qrCodeDTO.getSum());
                transaction.setDateTime(qrCodeDTO.getDateTime());
                sendMessage(update.getMessage().getFrom().getId(), "Введи категорию");
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            } catch (QRCodeRecognizeException e) {
                sendMessage(update.getMessage().getFrom().getId(), e.getMessage());
            }
        }
    }

    /**
     * Обработчик присланного документа
     */
    private void documentHandler(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        if (operation == Operation.INPUT_FILE) {
            try {
                TableFileReader fileReader = new SberTableFileReader();
                List<Transaction> transactions = fileReader.read(userId,
                        inputTableFile(update.getMessage().getDocument()));
                if (transactions.isEmpty()) {
                    throw new NullPointerException();
                }
                System.out.println(transactions);
                transactionService.addTransactions(transactions);
                String text = "Данные сохранены";
                // TODO: обработать случай, если данные не занесутся
                sendMessage(userId, text);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                sendMessage(userId, "Произошла ошибка при чтении данных из файла.");
                sendInputDataMenu(userId);
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
            case "fileFromBank" -> fileFromBank(userId);
            case "manualInput" -> manualInput(userId);
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
        transaction = new Transaction(id);
        operation = Operation.QR_PHOTO;
        sendMessage(id, "Пришли фото чека, содержащего QR-код");
    }

    private String inputQRPhoto(List<PhotoSize> photos) throws QRCodeRecognizeException,
            IOException, TelegramApiException {
        PhotoSize photo = photos.getLast();
        String photoPath = getFilePath(photo.getFileId());
        return QRCodeReader.decodeQR(QRCodeReader.downloadPhoto(photoPath));
    }

    private void fileFromBank(Long id) {
        transaction = new Transaction(id);
        System.out.println("fileFromBank");
        String text = """
                Я умею считывать информацию с файлов форматов .xlsx/.xls.\
                Ты можешь выгрузить информацию о расходах из сайта банка (СБЕР, Т-Банк) за определенный\
                период, а я внесу необходимую информацию в базу.
                
                Пришли мне файл с информацией о расходах в формате .xlsx/.xls""";
        sendMessage(id, text);
        operation = Operation.INPUT_FILE;
    }

    private void manualInput(Long id) {
        transaction = new Transaction(id);
        System.out.println("manualInput");
        String text = """
                Ты можешь внести конкретную трату, а я внесу необходимую информацию в базу.
                
                Введи дату операции:
                """;
        sendMessage(id, text);
        operation = Operation.INPUT_DATE;
    }

    private String inputTableFile(Document document) throws TelegramApiException {
       return getFilePath(document.getFileId());
    }


    /**
     * Метод для получения расположения файла в облаке Telegram
     * @param fileId строковый идентификатор файла
     * @throws TelegramApiException
     */
    private String getFilePath(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File file = execute(getFile);
        return "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
    }
}
