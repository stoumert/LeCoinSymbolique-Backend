package com.example.demo.configuration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * SmsConfig est une classe utilitaire conçue pour faciliter l'envoi de messages SMS.
 * Elle utilise l'API d'un fournisseur de service SMS externe pour envoyer les messages.
 *
 * Cette classe comprend des méthodes pour s'intégrer avec le service SMS-To
 * pour l'envoi de messages SMS vers des numéros de téléphone spécifiés.
 */
public class SmsConfig {
    private static final String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
            "eyJpc3MiOiJodHRwczovL2F1dGg6ODA4MC9hcGkvdjEvdXNlcnMvYXBpL2tleXMvZ2VuZ" +
            "XJhdGUiLCJpYXQiOjE3NDczMzE2OTMsIm5iZiI6MTc0NzMzMTY5MywianRpIjoicDVWNUk" +
            "yb2dlWHRGQjRhUCIsInN1YiI6NDgxMjMxLCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0" +
            ".c02bxmKitCS5QZMCxjz3qn6Wgg3gt_mv78_Rw5fJ5KM";

    public static void sendSmsWithSMSTo(String phoneNumber, String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("to", phoneNumber);
        requestBody.put("message", "Votre code de validation est : " + code);
        requestBody.put("sender_id", "MyApp");
        requestBody.put("bypass_optout", true);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.sms.to/sms/send",
                request,
                String.class
        );

        //System.out.println("SMS response: " + response.getBody());
    }


}
