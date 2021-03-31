package de.morgroup.eventplaner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import de.morgroup.eventplaner.R;

public class EventUebersichtActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventuebersicht);
    }

    // Logout-Button für Menü
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(),LoginActivity.class)); //startet LoginActivity
        finish(); //benendet aktuelle Acitivity
    }
}