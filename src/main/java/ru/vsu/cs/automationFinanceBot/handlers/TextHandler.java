package ru.vsu.cs.automationFinanceBot.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.vsu.cs.automationFinanceBot.context.OperationContext;
import ru.vsu.cs.automationFinanceBot.context.TransactionContext;
import ru.vsu.cs.automationFinanceBot.model.dto.AnalysisDTO;
import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.model.enums.Command;
import ru.vsu.cs.automationFinanceBot.model.enums.Operation;
import ru.vsu.cs.automationFinanceBot.parsers.file.ExcelTableFileWriter;
import ru.vsu.cs.automationFinanceBot.services.MessageService;
import ru.vsu.cs.automationFinanceBot.services.TransactionService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Обработчик текста. Реализация {@link UpdateHandler}
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TextHandler implements UpdateHandler {
    private DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final OperationContext operationContext;
    private final TransactionContext transactionContext;
    private final MessageService messageService;
    private final TransactionService transactionService;
    private Transaction transaction;
    private AnalysisDTO analysisDTO;

    /**
     * Метод, проверяющий поддержку метода
     *
     * @param update сообщение от пользователя
     * @return {@link Boolean} значение: True - поддерживается, False - не поддерживается.
     */
    @Override
    public boolean supports(Update update) {
        return update.hasMessage() && update.getMessage().hasText() &&
                !update.getMessage().getText().startsWith(Command.PREFIX.getCommandName());
    }

    /**
     * Обработчик текстового сообщения
     *
     * @param update сообщение от пользователя
     * @param bot экземпляр бота
     */
    @Override
    public void handle(Update update, AbsSender bot) {
        var message = update.getMessage();
        var user = message.getFrom();
        log.info(user.getFirstName() + " написал: " + message.getText());

        switch (operationContext.getOperation(user.getId())) {
            case QR_PHOTO -> {
                messageService.sendMessage(user.getId(),
                        "Некорректный ввод. Пришли фото чека, содержащего QR-код", bot);
            }
            case INPUT_DATE -> {
                try {
                    transaction = transactionContext.getTransaction(user.getId());
                    transaction.setDateTime(LocalDateTime.parse(update.getMessage().getText(), DATE_FORMAT));
                    messageService.sendMessage(user.getId(), "Введи сумму расхода:", bot);
                    transactionContext.setOperation(user.getId(), transaction);
                    operationContext.setOperation(user.getId(), Operation.INPUT_SUM);
                } catch (DateTimeParseException e) {
                    log.warn(e.getMessage());
                    messageService.sendMessage(user.getId(),
                            "Неверный формат даты. Введите в формате: '01.01.2020 18:53'",
                            bot);
                }
            }
            case INPUT_DATE_START -> {
                try {
                    analysisDTO = new AnalysisDTO(user.getId());
                    analysisDTO.setDateFrom(LocalDateTime.parse(update.getMessage().getText(), DATE_FORMAT));
                    messageService.sendMessage(user.getId(), "Введи дату окончания анализа:", bot);
                    operationContext.setOperation(user.getId(), Operation.INPUT_DATE_END);
                } catch (DateTimeParseException e) {
                    log.warn(e.getMessage());
                    messageService.sendMessage(user.getId(),
                            "Неверный формат даты. Введите в формате: '01.01.2020 18:53'",
                            bot);
                }
            }
            case INPUT_DATE_END -> {
                try {
                    analysisDTO.setDateTo(LocalDateTime.parse(update.getMessage().getText(), DATE_FORMAT));
                    messageService.sendMessage(user.getId(), "Идет сбор данных...", bot);
                    messageService.sendFile(user.getId(),
                            new ExcelTableFileWriter().write(transactionService.findTransactionsBetweenDateFromAndDateTo(analysisDTO),
                                                                transactionService.findTransactionsGroupByCategories(analysisDTO)),
                            bot);
                    operationContext.setOperation(user.getId(), Operation.MAIN_MENU);
                    messageService.sendMainMenu(user.getId(), bot);
                } catch (DateTimeParseException e) {
                    log.warn(e.getMessage());
                    messageService.sendMessage(user.getId(),
                            "Неверный формат даты. Введите в формате: '01.01.2020 18:53'",
                            bot);
                }
            }
            case INPUT_SUM -> {
                try {
                    transaction = transactionContext.getTransaction(user.getId());
                    transaction.setSum(Float.parseFloat(update.getMessage().getText()));
                    messageService.sendMessage(user.getId(), "Введи категорию расхода:", bot);
                    transactionContext.setOperation(user.getId(), transaction);
                    operationContext.setOperation(user.getId(), Operation.INPUT_CATEGORY);

                } catch (NumberFormatException e) {
                    messageService.sendMessage(user.getId(),
                            "Неверный формат числа. Введите в одном из форматах: '255', '255.99'",
                            bot);
                }
            }
            case INPUT_CATEGORY -> {
                transaction = transactionContext.getTransaction(user.getId());
                transaction.setCategory(update.getMessage().getText());
                messageService.sendMessage(user.getId(), "Введи описание:", bot);
                transactionContext.setOperation(user.getId(), transaction);
                operationContext.setOperation(user.getId(), Operation.INPUT_DESCRIPTION);
            }
            case INPUT_DESCRIPTION -> {
                transaction = transactionContext.getTransaction(user.getId());
                transaction.setDescription(update.getMessage().getText());
                transactionContext.setOperation(user.getId(), transaction);
                transactionService.addTransaction(transaction);
                // TODO: Сделать обработку ошибок на случай, если трата не сохранится
                messageService.sendMessage(user.getId(), "Трата сохранена.", bot);
                // TODO: Вывести юзеру информацию о поступившей трате
                messageService.sendMainMenu(user.getId(), bot);
            }
            case null, default -> {
                messageService.sendMessage(user.getId(), "Некорректный ввод.", bot);
                messageService.sendMainMenu(user.getId(), bot);
            }
        }
    }
}
