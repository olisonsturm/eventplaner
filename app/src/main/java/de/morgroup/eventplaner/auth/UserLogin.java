package de.morgroup.eventplaner.auth;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.view.activity.ConfirmActivity;
import de.morgroup.eventplaner.view.activity.MainActivity;

public abstract class UserLogin {

    Activity activity;
    FirebaseAuth firebaseAuth;

    protected UserLogin(Activity activity, FirebaseAuth firebaseAuth) {
        this.activity = activity;
        this.firebaseAuth = firebaseAuth;
    }

    public abstract void login();

    protected void loggedInSuccessfully() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if (task.getResult().exists()) {
                    // user exists
                    Toast.makeText(activity, activity.getResources().getString(R.string.loginSuccessful), Toast.LENGTH_SHORT).show();
                    activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                    activity.finish();
                } else {
                    // user registration not completed
                    activity.startActivity(new Intent(activity.getApplicationContext(), ConfirmActivity.class));
                    activity.finish();
                }
            });
        }
    }

    protected void authFailure(Task task) {
        Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
    }

}
