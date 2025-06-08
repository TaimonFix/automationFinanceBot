package ru.vsu.cs.automationFinanceBot.bot.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class BotToken {
    private String token;

    public BotToken() {
        this.token = readToken();
    }

    public String getToken() {
        return token;
    }

    private String readToken() {
        try {
            return Files.readString(Path.of("src/main/java/ru/vsu/cs/automationFinanceBot/bot/data/botToken.txt"));
        } catch (IOException e) {
            log.error("Произошла ошибка при чтении токена.");
        }
        return null;


    }
}
