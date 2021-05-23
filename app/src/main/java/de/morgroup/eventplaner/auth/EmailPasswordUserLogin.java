package de.morgroup.eventplaner.auth;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import de.morgroup.eventplaner.R;

public class EmailPasswordUserLogin extends UserLogin {

    EditText email, password;

    private EmailPasswordUserLogin(Activity activity, FirebaseAuth firebaseAuth, EditText email, EditText password) {
        super(activity, firebaseAuth);
        this.email = email;
        this.password = password;
    }

    public static EmailPasswordUserLogin create(Activity activity, FirebaseAuth firebaseAuth, EditText email, EditText password) {
        return new EmailPasswordUserLogin(activity, firebaseAuth, email, password);
    }

    @Override
    public void login() {

        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        // Interrogate errors
        if (TextUtils.isEmpty(email)) {
            this.email.setError(activity.getResources().getString(R.string.eMailRequired));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            this.password.setError(activity.getResources().getString(R.string.passwordRequired));
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loggedInSuccessfully();
            } else {
                authFailure(task);
            }
        });
    }
}
