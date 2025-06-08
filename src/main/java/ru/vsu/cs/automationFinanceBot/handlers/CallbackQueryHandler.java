package ru.vsu.cs.automationFinanceBot.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.vsu.cs.automationFinanceBot.context.OperationContext;
import ru.vsu.cs.automationFinanceBot.context.TransactionContext;
import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.model.enums.Operation;
import ru.vsu.cs.automationFinanceBot.services.MessageService;

/**
 * Обработчик нажатых кнопок. Реализация {@link UpdateHandler}
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryHandler implements UpdateHandler {

    private final MessageService messageService;
    private final TransactionContext transactionContext;
    private final OperationContext operationContext;

    /**
     * Метод, проверяющий поддержку метода
     *
     * @param update сообщение от пользователя
     * @return {@link Boolean} значение: True - поддерживается, False - не поддерживается.
     */
    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery();
    }

    /**
     * Обработчик нажатой кнопки
     *
     * @param update сообщение от пользователя
     * @param bot    экземпляр бота
     */
    @Override
    public void handle(Update update, AbsSender bot) {
        var callbackData = update.getCallbackQuery().getData();
        var userId = update.getCallbackQuery().getFrom().getId();
        log.info(userId + " нажал на кнопку: " + callbackData);
        switch (callbackData) {
            case "qr" -> qr(userId, bot);
            case "fileFromBank" -> fileFromBank(userId, bot);
            case "manualInput" -> manualInput(userId, bot);
            case "mainMenu" -> messageService.sendMainMenu(userId, bot);
            case "inputData" -> messageService.sendInputDataMenu(userId, bot);
            case "analysis" -> messageService.sendAnalysisDataMenu(userId, bot);
        }
    }

    /**
     * Начать добавление транзакции при помощи отправления QR кода
     *
     * @param id идентификатор пользователя
     * @param bot экземпляр бота
     */
    private void qr(Long id, AbsSender bot) {
        transactionContext.setOperation(id, new Transaction(id));
        operationContext.setOperation(id, Operation.QR_PHOTO);
        messageService.sendMessage(id, "Пришли фото чека, содержащего QR-код", bot);
    }

    /**
     * Начать добавление транзакции при помощи отправления файла с выгрузкой расходов из банка
     *
     * @param id идентификатор пользователя
     * @param bot экземпляр бота
     */
    private void fileFromBank(Long id, AbsSender bot) {
        transactionContext.setOperation(id, new Transaction(id));
        System.out.println("fileFromBank");
        String text = """
                Я умею считывать информацию с файлов форматов .xlsx/.xls. \
                Ты можешь выгрузить информацию о расходах из сайта банка (СБЕР, Т-Банк) за определенный     \
                период, а я внесу необходимую информацию в базу.
                
                Пришли мне файл с информацией о расходах в формате .xlsx/.xls""";
        messageService.sendMessage(id, text, bot);
        operationContext.setOperation(id, Operation.INPUT_FILE);
    }

    /**
     * Начать добавление транзакции при помощи ручного ввода
     *
     * @param id идентификатор пользователя
     * @param bot экземпляр бота
     */
    private void manualInput(Long id, AbsSender bot) {
        transactionContext.setOperation(id, new Transaction(id));
        System.out.println("manualInput");
        String text = """
                Ты можешь внести конкретную трату, а я внесу необходимую информацию в базу.
                
                Введи дату операции:
                """;
        messageService.sendMessage(id, text, bot);
        operationContext.setOperation(id, Operation.INPUT_DATE);
    }
}
