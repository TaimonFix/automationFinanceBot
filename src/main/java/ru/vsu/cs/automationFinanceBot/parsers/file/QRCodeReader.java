package ru.vsu.cs.automationFinanceBot.parsers.file;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import ru.vsu.cs.automationFinanceBot.model.dto.QRCodeDTO;
import ru.vsu.cs.automationFinanceBot.exceptions.QRCodeRecognizeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

import static ru.vsu.cs.automationFinanceBot.model.dto.QRCodeDTO.FORMATTER;

/**
 * Класс для работы с QR-кодом
 */
public class QRCodeReader {

    /**
     * Статический метод, позволяющий получить изображение в буферизованном виде
     * @param photoPath путь, по которому расположено изображение
     * @return изображение
     * @throws IOException при отсутствии изображения
     */
    public static BufferedImage downloadPhoto(String photoPath) throws IOException {
        URL url = new URL(photoPath);
        return ImageIO.read(url);
    }

    /**
     * Статический метод, позволяющий получить содержимое QR-кода
     * @param bufferedImage Исходное изображение с QR-кодом
     * @return Строка с данными из QR-кода
     */
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

    /**
     * Статический метод, позволяющий распарсить QR-код по элементам
     * @param qr текст, полученный при чтении QR-кода
     * @return экземпляр класса QRCode, содержащий элементы QR-кода
     */
    public static QRCodeDTO parse(String qr) {
        QRCodeDTO qrCodeDTO = new QRCodeDTO();

        for (String arg: qr.split("&")) {
            String[] param = arg.split("=");

            switch (param[0]) {
                case "t" -> qrCodeDTO.setDateTime(LocalDateTime.parse(param[1], FORMATTER));
                case "s" -> qrCodeDTO.setSum(Float.parseFloat(param[1]));
                case "fn" -> qrCodeDTO.setFn(Long.parseLong(param[1]));
                case "i" -> qrCodeDTO.setI(Integer.parseInt(param[1]));
                case "fp" -> qrCodeDTO.setFp(Long.parseLong(param[1]));
                case "n" -> qrCodeDTO.setN(Integer.parseInt(param[1]));
            }
        }

        return qrCodeDTO;
    }
}
