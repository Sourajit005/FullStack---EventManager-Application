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

    @Async
    public void sendRegistrationConfirmation(String toEmail, String eventTitle, byte[] qrCodeImage) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(toEmail);
            helper.setSubject("Registration Confirmed: " + eventTitle);

            String htmlContent = "<h1>Registration Confirmed!</h1>" +
                    "<p>You are officially registered for <b>" + eventTitle + "</b>.</p>" +
                    "<p>Please show the attached QR code at the event entrance.</p>" +
                    "<p>Thank you!</p>" +
                    "<img src='cid:registrationQR'>"; 

            helper.setText(htmlContent, true); 
            helper.addInline("registrationQR", new ByteArrayResource(qrCodeImage), "image/png");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}