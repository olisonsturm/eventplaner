package de.morgroup.eventplaner.view.activity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;

public class RegisterActivity extends Activity {

    // fb
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // view
    @BindView(R.id.eMailEditText)
    EditText eMailEditText;
    @BindView(R.id.passwordEditText)
    EditText passwordEditText;
    @BindView(R.id.passwordAgainEditText)
    EditText passwordAgainEditText;

    private String email;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.loginLinkButton)
    void onLoginLinkClick() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @OnClick(R.id.passwordAgainEditText)
    void onEnterPress() {
        register();
    }

    @OnClick(R.id.registerButton)
    void onRegisterClick() {
        register();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void register() {
        // check norm has been complied
        if (validate()) {
            // Email-Password-Registration
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sendEmailVerification();
                    startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                    finish();
                } else {
                    eMailEditText.setError(task.getException().getMessage());
                }
            });
        }
    }

    private boolean validate() {
        // get text input
        email = eMailEditText.getText().toString().trim().toLowerCase();
        password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        // check input-information
        if (TextUtils.isEmpty(email)) {
            eMailEditText.setError(getResources().getString(R.string.eMailRequired));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getResources().getString(R.string.passwordRequired));
            return false;
        }
        if (TextUtils.isEmpty(passwordAgain)) {
            passwordEditText.setError(getResources().getString(R.string.passwordAgainRequired));
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError(getResources().getString(R.string.passwordLenght));
            return false;
        }
        if (!TextUtils.equals(password, passwordAgain)) {
            passwordAgainEditText.setError(getResources().getString(R.string.passwordDoNotMatch));
            return false;
        }
        return true;
    }

    public void sendEmailVerification() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // email sent
                    }
                });
    }

}
