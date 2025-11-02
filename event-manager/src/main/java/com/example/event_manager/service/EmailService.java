package com.example.event_manager.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a registration confirmation email with a QR code attachment.
     * Runs asynchronously.
     *
     * @param toEmail     The recipient's email address.
     * @param eventTitle  The title of the event.
     * @param qrCodeImage The byte array of the QR code PNG.
     */
    @Async
    public void sendRegistrationConfirmation(String toEmail, String eventTitle, byte[] qrCodeImage) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // 'true' indicates this is a multipart message (needed for attachments)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(toEmail);
            helper.setSubject("Registration Confirmed: " + eventTitle);

            // Create simple HTML content
            String htmlContent = "<h1>Registration Confirmed!</h1>" +
                    "<p>You are officially registered for <b>" + eventTitle + "</b>.</p>" +
                    "<p>Please show the attached QR code at the event entrance.</p>" +
                    "<p>Thank you!</p>" +
                    "<img src='cid:registrationQR'>"; // 'cid' for content ID

            helper.setText(htmlContent, true); // 'true' indicates HTML

            // Attach the QR code as an "inline" resource
            helper.addInline("registrationQR", new ByteArrayResource(qrCodeImage), "image/png");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // In a real app, you'd have better error logging
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}