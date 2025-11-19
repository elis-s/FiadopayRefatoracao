package edu.ucsal.fiadopay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meu-webhook")
public class WebhookTestController {

    @PostMapping
    public ResponseEntity<String> receberWebhook(@RequestBody String payload) {
        System.out.println("📩 WEBHOOK RECEBIDO:");
        System.out.println(payload);
        System.out.println("===================================");

        // Sempre responde 200 OK
        return ResponseEntity.ok("webhook recebido");
    }
}
