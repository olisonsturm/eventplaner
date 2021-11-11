package de.morgroup.eventplaner;

import android.app.Application;
import android.view.textclassifier.TextClassifierEvent;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;

public class EventPlaner extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // app initialization logic
        FirebaseAuth.getInstance().useAppLanguage();
        FirebaseApp.initializeApp(this);

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());
    }
}
