package com.pccw.registrationservice.notification;

import com.pccw.registrationservice.notification.dto.SimpleMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SimpleMailService {

    @Value("${pccw.email.sender}")
    private String sender;

    private final JavaMailSender mailSender;

    public SimpleMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(SimpleMail message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(message.getMailTo());
            mailMessage.setSubject(message.getSubject());
            mailMessage.setText(message.getBody());

            mailSender.send(mailMessage);
        } catch(Exception e) {
            // log error but do not propagate
            log.error("Error encountered while sending email", e);
        }
    }

}
