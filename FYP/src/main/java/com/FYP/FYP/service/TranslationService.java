// package com.FYP.FYP.service;

// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.cloud.translate.Translate;
// import com.google.cloud.translate.TranslateOptions;
// import com.google.cloud.translate.Translation;
// import org.springframework.stereotype.Service;

// import java.io.FileInputStream;
// import java.io.IOException;

// @Service
// public class TranslationService {

//     private final Translate translate;

//     public TranslationService() {
//         try (FileInputStream credentialsStream = new FileInputStream("D:\\University\\Year 4\\FYP\\project\\lustrous-drake-451510-r6-48540070a596.json")) {
//             GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
//             this.translate = TranslateOptions.newBuilder()
//                     .setCredentials(credentials)
//                     .build()
//                     .getService();
//         } catch (IOException e) {
//             throw new RuntimeException("Failed to load Google Translate credentials", e);
//         }
//     }

//     public String translate(String text, String targetLang) {
//         Translation translation = translate.translate(text,
//                 Translate.TranslateOption.targetLanguage(targetLang),
//                 Translate.TranslateOption.format("text"));

//         return translation.getTranslatedText();
//     }
// }
