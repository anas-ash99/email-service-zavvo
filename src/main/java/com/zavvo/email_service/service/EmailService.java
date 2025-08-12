package com.zavvo.email_service.service;

import com.zavvo.email_service.enums.EmailType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendPaymentEmail(String toEmail, EmailType emailType  , String giftCardNumber) throws MessagingException {
        String subject = getSubject(emailType);
        String content = buildEmailTemplate(emailType, giftCardNumber);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(content, true); // true for HTML
        helper.setFrom("anas.ash099@gmail.com");

        mailSender.send(message);
    }


    private String buildEmailTemplate(EmailType type, String giftCardNumber) {
        switch (type){
            case PAYMENT_SUCCESSFUL -> {
                return """
                    <div style="font-family:Arial,sans-serif;padding:20px;border:1px solid #e0e0e0;border-radius:10px;">
                        <h2 style="color:#2e7d32;">✅ Payment Successful!</h2>
                        <p>Thank you for your purchase. Your payment has been processed successfully.</p>
                        <p><strong>Your Gift Card Number:</strong></p>
                        <div style="padding:15px;background-color:#f1f8e9;border:1px dashed #81c784;margin:20px 0;font-size:20px;">
                            %s
                        </div>
                        <p>Enjoy your gift card! 🎁</p>
                        <hr>
                        <small>If you have any questions, contact us at anas.ash099@gmail.com</small>
                    </div>
                    """.formatted(giftCardNumber);
            }
            case PAYMENT_FAILURE -> {
                return """
                    <div style="font-family:Arial,sans-serif;padding:20px;border:1px solid #e0e0e0;border-radius:10px;">
                        <h2 style="color:#c62828;">❌ Payment Failed</h2>
                        <p>Unfortunately, we couldn't process your payment at this time.</p>
                        <p>Please double-check your payment details and try again.</p>
                        <a href="https://Zavvo.com/retry-payment" style="display:inline-block;margin-top:20px;padding:10px 20px;background-color:#ef5350;color:white;text-decoration:none;border-radius:5px;">Try Again</a>
                        <hr>
                        <small>If you continue to experience issues, contact us at anas.ash099@gmail.com</small>
                    </div>
                    """;
            }
            case null, default -> {
                return "";
            }
        }
    }


    private String getSubject(EmailType type){
        switch (type){
            case PAYMENT_SUCCESSFUL -> {
                return "🎉 Payment Successful - Your Gift Card";
            }
            case PAYMENT_FAILURE -> {
                return "❌ Payment Failed";

            }
            case null, default -> {
                return "";
            }
        }
    }

}
