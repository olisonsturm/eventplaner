package de.morgroup.eventplaner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import de.morgroup.eventplaner.R;

public class RegisterActivity extends Activity {

    // init
    EditText eMailEditText, passwordEditText, passwordAgainEditText;
    Button registerButton;
    TextView loginLinkButton;
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Deklaration
        eMailEditText = findViewById(R.id.eMailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        registerButton = findViewById(R.id.registerButton);
        loginLinkButton = findViewById(R.id.loginLinkButton);

        firebaseAuth = FirebaseAuth.getInstance();

        // bereits eingeloggt?
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // Tastatur ok
        passwordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    register();
                    handled = true;
                }
                return handled;
            }
        });

        // Button Register pressed
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        // Link zur LoginActivity
        loginLinkButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                   startActivity(intent);
                                                   finish();
                                               }
                                           }
        );
    }

    private void register() {
        // toString
        String email = eMailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        // Interrogate errors
        if (TextUtils.isEmpty(email)) {
            eMailEditText.setError(getResources().getString(R.string.eMailRequired));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getResources().getString(R.string.passwordRequired));
            return;
        }

        if (TextUtils.isEmpty(passwordAgain)) {
            passwordEditText.setError(getResources().getString(R.string.passwordAgainRequired));
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError(getResources().getString(R.string.passwordLenght));
            return;
        }

        if (!TextUtils.equals(password, passwordAgain)) {
            passwordAgainEditText.setError(getResources().getString(R.string.passwordDoNotMatch));
            return;
        }

        // Benutzer-Registration
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.accountCreated), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
