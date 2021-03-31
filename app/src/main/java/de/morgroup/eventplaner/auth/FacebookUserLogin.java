package de.morgroup.eventplaner.auth;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class FacebookUserLogin extends UserLogin {

    public FacebookUserLogin(Activity activity, FirebaseAuth firebaseAuth) {
        super(activity, firebaseAuth);
    }

    public static FacebookUserLogin create(Activity activity, FirebaseAuth firebaseAuth) {
        return new FacebookUserLogin(activity, firebaseAuth);
    }

    @Override
    public void login() {
        Toast.makeText(activity, "in progress...", Toast.LENGTH_SHORT).show();
        //LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
    }

    public void result(int requestCode, int resultCode, Intent data, CallbackManager callbackManager) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        loggedInSuccessfully();
                    } else {
                        authFailure(task);
                    }
                });
    }
}
