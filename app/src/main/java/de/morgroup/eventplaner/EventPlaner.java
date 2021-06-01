package de.morgroup.eventplaner;

import android.app.Application;
import android.view.textclassifier.TextClassifierEvent;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class EventPlaner extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // app initialization logic
        FirebaseAuth.getInstance().useAppLanguage();
        FirebaseApp.initializeApp(this);
    }
}
