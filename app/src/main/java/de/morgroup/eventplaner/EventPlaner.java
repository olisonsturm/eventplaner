package de.morgroup.eventplaner;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class EventPlaner extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // app initialization logic
        FirebaseApp.initializeApp(this);
    }
}
