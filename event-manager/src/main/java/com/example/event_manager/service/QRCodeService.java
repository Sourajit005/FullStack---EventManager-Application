package com.example.event_manager.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class QRCodeService {

    /**
     * Generates a QR code image from text.
     *
     * @param text  The text to encode in the QR code.
     * @param width The width of the QR code image.
     * @param height The height of the QR code image.
     * @return A byte array containing the PNG image.
     */
    public byte[] generateQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();

        } catch (Exception e) {
            // In a real app, handle this exception more gracefully
            throw new RuntimeException("Could not generate QR code", e);
        }
    }
}