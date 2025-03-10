package ru.vsu.cs.automationFinanceBot.bot;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QRCodeReader {

    public static String decodeQR(File qrCodeImage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeImage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage); // преобразование изображения в градации серого
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source)); // алгоритм бинаризации (черное-белое)

        try {
            Result result = new MultiFormatReader().decode(bitmap); // анализ изображения
            return result.getText(); // Строка с данными из QR-кода
        } catch (NotFoundException e) {
            return "QR-код не найден";
        }
    }

    public static void main(String[] args) throws IOException {
        File qrCodeFile = new File("qr.png");
        String decodeText = decodeQR(qrCodeFile);
        System.out.println(decodeText);
    }
}
