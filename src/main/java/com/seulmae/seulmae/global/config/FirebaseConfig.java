package com.seulmae.seulmae.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {
    @Value("${spring.firebase.privateKey}")
    private String PRIVATE_KEY;
    @PostConstruct
    public void init() {
        try (InputStream serviceAccount = new ClassPathResource(PRIVATE_KEY).getInputStream()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            log.error("Error initializing FirebaseApp", e.getMessage());
        }
    }
}
