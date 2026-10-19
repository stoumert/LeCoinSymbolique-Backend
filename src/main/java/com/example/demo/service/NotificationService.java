package com.example.demo.service;

import com.example.demo.Entites.Validation;
import com.example.demo.configuration.SmsConfig;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class NotificationService {

    JavaMailSender mailSender;
    public void sendNotification(Validation validation) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@leCoinSymbolique.fr");
        message.setTo(validation.getClient().getEmail());
        message.setSubject("Code de validation");
        String text = String.format("Bonjour %s, \n Votre code de validation est %s; A bientot."
                , validation.getClient().getUsername()
                ,validation.getValidationCode());
        message.setText(text);
        mailSender.send(message);
    }

    public void sendSms(Validation validation){
        String phoneNumber = validation.getClient().getPhoneNumber();
        String code = validation.getValidationCode();
        SmsConfig.sendSmsWithSMSTo(phoneNumber,code);
    }
}
