package ru.vsu.cs.automationFinanceBot.bot;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import ru.vsu.cs.automationFinanceBot.exception.QRCodeRecognizeException;

import java.awt.image.BufferedImage;

public class QRCodeReader {

    public static String decodeQR(BufferedImage bufferedImage) {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage); // преобразование изображения в градации серого
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source)); // алгоритм бинаризации (черное-белое)

        try {
            Result result = new MultiFormatReader().decode(bitmap); // анализ изображения
            return result.getText(); // Строка с данными из QR-кода
        } catch (NotFoundException e) {
            throw new QRCodeRecognizeException("Не удалось распознать QR-код.");
        }
    }
}
