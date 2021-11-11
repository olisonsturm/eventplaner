package de.morgroup.eventplaner.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import de.morgroup.eventplaner.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // wait
        new Handler().postDelayed(() -> {
            // checks whether logged in or not
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (firebaseUser != null) {
                db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
                    if (task.getResult().exists()) {
                        // user exists
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        // user registration not completed
                        startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                    }
                    finish();
                });
            } else {
                // user is not logged in
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }

        }, 250);

    }

}