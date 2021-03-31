package de.morgroup.eventplaner.auth;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class TwitterUserLogin extends UserLogin {

    public TwitterUserLogin(Activity activity, FirebaseAuth firebaseAuth) {
        super(activity, firebaseAuth);
    }

    public static TwitterUserLogin create(Activity activity, FirebaseAuth firebaseAuth) {
        return new TwitterUserLogin(activity, firebaseAuth);
    }

    @Override
    public void login() {
        Toast.makeText(activity, "in progress...", Toast.LENGTH_SHORT).show();
    }
}
