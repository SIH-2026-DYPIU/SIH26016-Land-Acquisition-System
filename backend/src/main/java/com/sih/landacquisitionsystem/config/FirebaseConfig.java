package com.sih.landacquisitionsystem.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Firebase initialization configuration.
 * Initializes Firebase Admin SDK using a service account file.
 * The path to the service account file should be provided via the FIREBASE_SERVICE_ACCOUNT_PATH environment variable.
 */
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            String serviceAccountPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH");
            if (serviceAccountPath == null || serviceAccountPath.isEmpty()) {
                // Fallback to default application credentials (useful when running on Google Cloud)
                FirebaseApp.initializeApp();
            } else {
                FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        }
    }
}