package ru.vsu.cs.automationFinanceBot.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vsu.cs.automationFinanceBot.bot.Bot;
import ru.vsu.cs.automationFinanceBot.context.OperationContext;
import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.model.enums.Operation;
import ru.vsu.cs.automationFinanceBot.parsers.file.SberTableFileReader;
import ru.vsu.cs.automationFinanceBot.parsers.file.TableFileReader;
import ru.vsu.cs.automationFinanceBot.services.MessageService;
import ru.vsu.cs.automationFinanceBot.services.TransactionService;

import java.util.List;

/**
 * Обработчик файлов. Реализация {@link UpdateHandler}
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentHandler implements UpdateHandler {

    private final OperationContext operationContext;
    private final MessageService messageService;
    private final TransactionService transactionService;

    /**
     * Метод, проверяющий поддержку метода
     *
     * @param update сообщение от пользователя
     * @return {@link Boolean} значение: True - поддерживается, False - не поддерживается.
     */
    @Override
    public boolean supports(Update update) {
        return update.hasMessage() && update.getMessage().hasDocument();
    }

    /**
     * Обработчик метода
     *
     * @param update сообщение от пользователя
     * @param bot    экземпляр бота
     */
    @Override
    public void handle(Update update, AbsSender bot) {
        Long userId = update.getMessage().getFrom().getId();
        log.info(userId + " прислал документ.");
        if (operationContext.getOperation(userId) == Operation.INPUT_FILE) {
            try {
                TableFileReader fileReader = new SberTableFileReader();
                List<Transaction> transactions = fileReader.read(userId,
                        ((Bot) bot).getFilePath(update.getMessage().getDocument().getFileId()));
                if (transactions.isEmpty()) {
                    throw new NullPointerException();
                }
                System.out.println(transactions);
                transactionService.addTransactions(transactions);
                String text = "Данные сохранены";
                // TODO: обработать случай, если данные не занесутся
                messageService.sendMessage(userId, text, bot);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                messageService.sendMessage(userId, "Произошла ошибка при чтении данных из файла.", bot);
                messageService.sendInputDataMenu(userId, bot);
            }
        }
    }


}
