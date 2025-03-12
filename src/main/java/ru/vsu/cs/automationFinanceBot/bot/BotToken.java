package ru.vsu.cs.automationFinanceBot.bot;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
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
            System.out.println("Произошла ошибка при чтении токена.");
        }
        return null;


    }
}
