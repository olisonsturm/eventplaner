package de.morgroup.eventplaner.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

import de.morgroup.eventplaner.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // checks whether logged in or not
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (firebaseUser != null) {
            db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.getResult().exists()) {
                    // user exists
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    // user registration not completed
                    startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                    finish();
                }
            });
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

}