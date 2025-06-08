package ru.vsu.cs.automationFinanceBot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vsu.cs.automationFinanceBot.bot.data.BotToken;
import ru.vsu.cs.automationFinanceBot.handlers.UpdateHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final BotToken botToken;

    @Value("${bot.name}")
    private String botName;

    private final List<UpdateHandler> handlers;
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
        for (UpdateHandler handler: handlers) {
            if (handler.supports(update)) {
                handler.handle(update, this);
            }
        }
    }

    /**
     * Метод для получения расположения файла в облаке Telegram
     * @param fileId строковый идентификатор файла
     * @throws TelegramApiException
     */
    public String getFilePath(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File file = execute(getFile);
        return "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
    }
}
