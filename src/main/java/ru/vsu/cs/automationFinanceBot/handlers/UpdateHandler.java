package ru.vsu.cs.automationFinanceBot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Интерфейс для обработки приходящих сообщений от пользователя
 */
public interface UpdateHandler {

    /**
     * Метод, проверяющий поддержку метода
     * @param update сообщение от пользователя
     * @return {@link Boolean} значение: True - поддерживается, False - не поддерживается.
     */
    boolean supports(Update update);

    /**
     * Обработчик метода
     *
     * @param update сообщение от пользователя
     * @param bot экземпляр бота
     */
    void handle(Update update, AbsSender bot);
}
