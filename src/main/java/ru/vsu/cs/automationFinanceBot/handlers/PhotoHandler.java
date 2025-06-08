package ru.vsu.cs.automationFinanceBot.handlers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vsu.cs.automationFinanceBot.bot.Bot;
import ru.vsu.cs.automationFinanceBot.context.OperationContext;
import ru.vsu.cs.automationFinanceBot.context.TransactionContext;
import ru.vsu.cs.automationFinanceBot.model.dto.QRCodeDTO;
import ru.vsu.cs.automationFinanceBot.model.entities.Transaction;
import ru.vsu.cs.automationFinanceBot.model.enums.Operation;
import ru.vsu.cs.automationFinanceBot.exceptions.QRCodeRecognizeException;
import ru.vsu.cs.automationFinanceBot.parsers.file.QRCodeReader;
import ru.vsu.cs.automationFinanceBot.services.MessageService;

import java.io.IOException;
import java.util.List;

/**
 * Обработчик фото. Реализация {@link UpdateHandler}
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoHandler implements UpdateHandler {

    private final OperationContext operationContext;
    private final MessageService messageService;
    private final TransactionContext transactionContext;

    /**
     * Метод, проверяющий поддержку метода
     *
     * @param update сообщение от пользователя
     * @return {@link Boolean} значение: True - поддерживается, False - не поддерживается.
     */
    @Override
    public boolean supports(Update update) {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }

    /**
     * Обработчик изображения
     *
     * @param update сообщение от пользователя
     * @param bot    экземпляр бота
     */
    @Override
    public void handle(Update update, AbsSender bot) {
        var userId = update.getMessage().getFrom().getId();
        log.info(userId + " прислал изображение");
        if (operationContext.getOperation(userId) == Operation.QR_PHOTO) {
            try {
                // TODO: Добавить обработку нескольких изображений
                QRCodeDTO qrCodeDTO = QRCodeReader.parse(inputQRPhoto(update.getMessage().getPhoto(), (Bot) bot));
                Transaction transaction = transactionContext.getTransaction(userId);
                transaction.setSum(qrCodeDTO.getSum());
                transaction.setDateTime(qrCodeDTO.getDateTime());
                transactionContext.setOperation(userId, transaction);
                messageService.sendMessage(userId, "Введи категорию", bot);
                operationContext.setOperation(userId, Operation.INPUT_CATEGORY);
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            } catch (QRCodeRecognizeException e) {
                messageService.sendMessage(userId, e.getMessage(), bot);
            }
        }
    }

    /**
     * Добавление фото, содержащего QR код
     *
     * @param photos список фото
     * @param bot экземпляр бота
     * @return путь, по которому расположено фото в базе Telegram
     * @throws QRCodeRecognizeException если QR код не был распознан
     * @throws IOException ошибка ввода-вывода
     * @throws TelegramApiException исключение на стороне Telegram
     */
    private String inputQRPhoto(List<PhotoSize> photos, Bot bot) throws QRCodeRecognizeException,
            IOException, TelegramApiException {
        PhotoSize photo = photos.getLast();
        String photoPath = bot.getFilePath(photo.getFileId());
        return QRCodeReader.decodeQR(QRCodeReader.downloadPhoto(photoPath));
    }
}
