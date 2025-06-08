package ru.vsu.cs.automationFinanceBot.handlers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.vsu.cs.automationFinanceBot.handlers.UpdateHandler;
import ru.vsu.cs.automationFinanceBot.model.enums.Command;
import ru.vsu.cs.automationFinanceBot.services.MessageService;

@RequiredArgsConstructor
@Component
@Slf4j
public class MenuCommandHandler implements UpdateHandler {

    private final MessageService messageService;
    /**
     * Метод, проверяющий поддержку команды
     *
     * @param update сообщение от пользователя
     * @return {@link Boolean} значение: True - поддерживается, False - не поддерживается.
     */
    @Override
    public boolean supports(Update update) {
        return update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText().startsWith(Command.MENU.getCommandName());
    }

    /**
     * Обработчик команды
     *
     * @param update сообщение от пользователя
     * @param bot    экземпляр бота
     */
    @Override
    public void handle(Update update, AbsSender bot) {
        var message = update.getMessage();
        var user = message.getFrom();
        log.info(user.getId() + " ввел команду: " + update.getMessage().getText());
        messageService.sendMessage(user.getId(), "Высылаю меню...", bot);
        messageService.sendMainMenu(user.getId(), bot);
    }
}
