package ru.vsu.cs.automationFinanceBot.bot;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {
    private final BotToken botToken;

    @Value("${bot.name}")
    private String botName;

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
        var message = update.getMessage();
        var user = message.getFrom();
        System.out.println(user.getFirstName() + " wrote " + message.getText());
        System.out.println(user.getId());

        if (update.hasMessage()) {
            if (message.getText().contains("/start")) {
                String text = "Привет! Я - финансовый помощник.\n\nЯ помогу тебе вести бюджет и оптимизировать твои траты." +
                        "\nДавай начнем с ввода данных. Я умею обрабатывать информацию следующими способами:\n" +
                        "1. Ты можешь отправить изображение с чеком, содержащим QR кодом - я его разберу и внесу " +
                        "траты в свой дневник.\n" +
                        "2. Я могу выгрузить данные с тратами из банка (например, СБЕР) и внести их в свой дневник.\n" +
                        "3. Ты можешь вручную вписать соответствующую трату.\n\n Выбери на клавиатуре соответствующее действие:";
                sendText(user.getId(), text);
            }
        }
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                         .chatId(who.toString()) // кому мы отправляем сообщение
                         .text(what).build(); // содержимое сообщения

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(); // здесь напечатается любая ошибка
        }
    }
}
