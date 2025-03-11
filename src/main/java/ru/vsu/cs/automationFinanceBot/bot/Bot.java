package ru.vsu.cs.automationFinanceBot.bot;
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
import ru.vsu.cs.automationFinanceBot.Command;
import ru.vsu.cs.automationFinanceBot.exception.QRCodeRecognizeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {
    private final BotToken botToken;

    private final String START = "/start";

    @Value("${bot.name}")
    private String botName;

    private InlineKeyboardMarkup keyboardMarkup;

    private InlineKeyboardButton button1;
    private InlineKeyboardButton button2;
    private InlineKeyboardButton button3;
    private InlineKeyboardButton button4;

    private Command command;

    public Bot() {
        this.botToken = new BotToken();
    }

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
                var message = update.getMessage();
                var user = message.getFrom();
                System.out.println(user.getFirstName() + " wrote " + message.getText());
                System.out.println(user.getId());

                if (update.hasMessage()) {
                    if (message.getText().contains(START)) {
                        String text = "Привет! Я - финансовый помощник.\n\nЯ помогу тебе вести бюджет и оптимизировать твои траты." +
                                "\nДавай начнем с ввода данных. Я умею обрабатывать информацию следующими способами:\n" +
                                "1. Ты можешь отправить изображение с чеком, содержащим QR код - я его разберу и внесу " +
                                "траты в свой дневник.\n" +
                                "2. Ты можешь прислать .xlsx/.csv файл с выгрузкой расходов за определенный период из банка " +
                                "(СБЕР, Т-Банк, Газпромбанк) - я проанализирую содержимое и внесу траты в свой дневник.\n" +
                                "3. Ты можешь вручную вписать соответствующую трату.";
                        sendMessage(user.getId(), text);
                        sendActionMenu(user.getId());

                    }
                }
            } else if (update.getMessage().hasPhoto()) {
                System.out.println(command);
                if (command == Command.QR) {
                    try {
                        System.out.println(inputQRPhoto(update.getMessage().getPhoto()));
                    } catch (IOException | TelegramApiException e) {
                        e.printStackTrace();
                    } catch (QRCodeRecognizeException e) {
                        sendMessage(update.getMessage().getFrom().getId(), e.getMessage());
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            var callbackData = update.getCallbackQuery().getData();
            var userId = update.getCallbackQuery().getFrom().getId();
            switch (callbackData) {
                case "qr" -> qr(userId);
                case "fileFromBank" -> fileFromBank();
                case "manualInput" -> manualInput();
                case "mainMenu" -> sendMainMenu(userId);
            }
        }
    }

    /**
     * Отправка сообщения пользователю
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
     * @param who id пользователя
     * @param txt текст меню
     * @param kb Экземпляр класса InlineKeyboardMarkup, содержащий необходимые кнопки
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
    private void sendActionMenu(Long id) {
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

    private void qr(Long id) {
        command = Command.QR;
        sendMessage(id, "Пришли фото чека, содержащего QR-код");
    }

    private String inputQRPhoto(List<PhotoSize> photos) throws QRCodeRecognizeException,
                                                        IOException, TelegramApiException {
        PhotoSize photo = photos.getLast();
        GetFile getFile = new GetFile();
        getFile.setFileId(photo.getFileId());
        File file = execute(getFile);
        String photoPath = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
        return QRCodeReader.decodeQR(downloadPhoto(photoPath));
    }

    private void fileFromBank() {
        System.out.println("fileFromBank");
    }

    private void manualInput() {
        System.out.println("manualInput");
    }

    private BufferedImage downloadPhoto(String photoPath) throws IOException {
        URL url = new URL(photoPath);
        return ImageIO.read(url);
    }
}
