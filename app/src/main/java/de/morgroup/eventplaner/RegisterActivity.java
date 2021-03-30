/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package de.morgroup.eventplaner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
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

import org.w3c.dom.Text;

public class RegisterActivity extends Activity {

    EditText eMailEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
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

        // Methoden

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

        // bereits eingeloggt?
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), EventUebersichtActivity.class));
            finish();
        }

        // Button Register pressed
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        // Link zur LoginActivity.java
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
        System.out.println("HALLOOOOOO MEIN FREUND");
        String email = eMailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        System.out.println(email +"--"+ password);
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        // Errors
        if (TextUtils.isEmpty(email)) {
            System.out.println("Email is Required");
            eMailEditText.setError("Email is Required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is Required");
            return;
        }

        if (TextUtils.isEmpty(passwordAgain)) {
            passwordEditText.setError("Password is Required");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be >= 6 characters");
            return;
        }

        if (!TextUtils.equals(password, passwordAgain)) {
            passwordAgainEditText.setError("Passwords do not match");
            return;
        }

        // Benutzer-Registration
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), EventUebersichtActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
