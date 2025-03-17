package com.FYP.FYP.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@RestController
public class TranslationController {

    private final Translate translate;

    public TranslationController() throws IOException {
        FileInputStream credentialsStream = new FileInputStream("D:/University/Year 4/FYP/project/lustrous-drake-451510-r6-48540070a596.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        this.translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    @PostMapping("/translate")
    public Map<String, String> translateText(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String targetLanguage = request.get("targetLanguage");

        Translation translation = translate.translate(message, Translate.TranslateOption.targetLanguage(targetLanguage));
        return Map.of("translatedText", translation.getTranslatedText());
    }
}
