//package com.example.SocialMedia.config.auth;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//@Configuration
//public class FirebaseConfig {
//
//    private static final String FIREBASE_SERVICE_ACCOUNT_FILE = "firebase-service-account.json";
//
//    @Bean
//    public FirebaseApp initializeFirebase() throws IOException {
//        ClassPathResource resource = new ClassPathResource(FIREBASE_SERVICE_ACCOUNT_FILE);
//
//        if (!resource.exists()) {
//            throw new IOException("Không tìm thấy file " + FIREBASE_SERVICE_ACCOUNT_FILE +
//                    ". Vui lòng tải về từ Firebase Console và đặt vào src/main/resources.");
//        }
//
//        InputStream serviceAccount = resource.getInputStream();
//
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//
//        if (FirebaseApp.getApps().isEmpty()) {
//            return FirebaseApp.initializeApp(options);
//        } else {
//            return FirebaseApp.getInstance();
//        }
//    }
//}
