package com.example.demo.service;

import com.example.demo.Entites.Client;
import com.example.demo.Entites.Validation;
import com.example.demo.repository.ValidationRepository;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Transactional
@AllArgsConstructor
@Service
public class ValidationService {
    private ValidationRepository validationRepository;
    private NotificationService notificationService;


    public void save(Client client) {
        Validation validation = generateValidation(client);
        notificationService.sendNotification(validation);
    }

    @NotNull
    private Validation generateValidation(Client client) {
        Validation validation = new Validation();
        validation.setClient(client);

        Instant creation = Instant.now();
        validation.setCreation(creation);

        Instant expiration = Instant.now().plus(10, MINUTES);
        validation.setExpiration(expiration);

        Random random = new Random();
        int randomInt = random.nextInt(999999);

        String code = String.format("%06d", randomInt);
        validation.setValidationCode(code);

        validationRepository.save(validation);
        return validation;
    }
    public void savePhoneNumber(Client client) {
        Validation validation = generateValidation(client);
        notificationService.sendSms(validation);
    }

    public Validation findByValidationCode(String validationCode) {
        return this.validationRepository.findByValidationCode(validationCode).orElseThrow((()-> new RuntimeException("votre code est invalide")));
    }

    /**
     * cette methode s'éxecute toutes les 15 minutes et nettoie la table validation
     * */
    @Scheduled(cron = "0 */15 * * * *")
    protected void deleteExpired() {
        validationRepository.deleteByExpirationBefore(Instant.now());
    }
}
