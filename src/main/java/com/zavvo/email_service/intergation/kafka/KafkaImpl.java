package com.zavvo.email_service.intergation.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zavvo.email_service.enums.EmailType;
import com.zavvo.email_service.service.EmailService;
import com.zavvo.shared.ExponentialBackoffRetry;
import com.zavvo.shared.model.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaImpl {

    private static final Logger logger = LoggerFactory.getLogger(KafkaImpl.class);
    @Autowired
    private KafkaTemplate<String, String> template;
    @Autowired
    private ExponentialBackoffRetry exponentialBackoffRetry;
    @Autowired
    private EmailService emailService;
    private final ObjectMapper objectMapper;

    public KafkaImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "card.created", groupId = "zavvo")
    public void cardCreated(String message) {
        try {
            logger.info("request received");
            sendEmail(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            retry(message);
        }
    }


    @KafkaListener(topics = "payment.failure",groupId = "zavvo")
    public void paymentFailed(String email){
        try {
            emailService.sendPaymentEmail(email, EmailType.PAYMENT_FAILURE, "");
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    private void retry(String message) {
        try {
            exponentialBackoffRetry.executeWithRetry(() -> {
                try {
                    sendEmail(message);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            logger.info("sending email failed, sending a message to email.failure");
            publishMessage("email.failure", message);
        }
    }

    private void sendEmail(String message) throws Exception {
        PaymentRequest paymentRequest = objectMapper.readValue(message, PaymentRequest.class);
        emailService.sendPaymentEmail(paymentRequest.getEmail(), EmailType.PAYMENT_SUCCESSFUL, paymentRequest.getCardNumber());
        logger.info("email has been successfully sent to {}", paymentRequest.getEmail());
    }

    public void publishMessage(String topic, String message) {
        template.send(topic, message);
    }


}
