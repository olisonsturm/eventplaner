    package de.morgroup.eventplaner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import de.morgroup.eventplaner.R;

public class EventUebersichtActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        LinearLayout linearLayout = new LinearLayout(this);
        setContentView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        EditText textView = new EditText(this);
        textView.setText (firebaseAuth.getCurrentUser().getDisplayName() + "-" + firebaseAuth.getCurrentUser().getEmail() + "-" + firebaseAuth.getCurrentUser().getUid() + "-" + firebaseAuth.getCurrentUser().getPhotoUrl());
        linearLayout.addView(textView);
        Button btn = new Button(this);
        btn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut(); //logout
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //startet LoginActivity
            finish(); //benendet aktuelle Acitivity
        });
        linearLayout.addView(btn);
    }


    // Logout-Button für Menü
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //startet LoginActivity
        finish(); //benendet aktuelle Acitivity
    }
}