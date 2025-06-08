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
public class StartCommandHandler implements UpdateHandler {

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
                update.getMessage().getText().startsWith(Command.START.getCommandName());
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
        String text = """
                    Привет! Я - финансовый помощник.

                    Я помогу тебе вести бюджет и оптимизировать твои траты.\

                    Давай начнем с ввода данных. Я умею обрабатывать информацию следующими способами:
                    1. Ты можешь отправить изображение с чеком, содержащим QR код - я его разберу и внесу \
                    траты в свой дневник.
                    2. Ты можешь прислать .xlsx/.csv файл с выгрузкой расходов за определенный период из банка \
                    (СБЕР, Т-Банк, Газпромбанк) - я проанализирую содержимое и внесу траты в свой дневник.
                    3. Ты можешь вручную вписать соответствующую трату.""";
        messageService.sendMessage(user.getId(), text, bot);
        messageService.sendInputDataMenu(user.getId(), bot);
    }
}
