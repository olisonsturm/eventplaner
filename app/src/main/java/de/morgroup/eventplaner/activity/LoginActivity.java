package de.morgroup.eventplaner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.auth.EmailPasswordUserLogin;
import de.morgroup.eventplaner.auth.FacebookUserLogin;
import de.morgroup.eventplaner.auth.GoogleUserLogin;
import de.morgroup.eventplaner.auth.TwitterUserLogin;
import de.morgroup.eventplaner.db.User;

public class LoginActivity extends Activity {

    // init
    private Activity activity = this;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;

    private EditText eMailEditText, passwordEditText;
    private Button loginButton;
    private ImageView googleLoginButton, facebookLoginButton, twitterLoginButton;
    private TextView registerLinkButton, forgotPasswordTextView;

    private EmailPasswordUserLogin emailPasswordUserLogin;
    private GoogleUserLogin googleUserLogin = GoogleUserLogin.create(activity, firebaseAuth);
    private FacebookUserLogin facebookUserLogin = FacebookUserLogin.create(activity, firebaseAuth);
    private TwitterUserLogin twitterUserLogin = TwitterUserLogin.create(activity, firebaseAuth);

    // Facebook
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseUser = firebaseAuth.getCurrentUser();

        // Deklaration
        eMailEditText = findViewById(R.id.eMailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        facebookLoginButton = findViewById(R.id.facebookLoginButton);
        twitterLoginButton = findViewById(R.id.twitterLoginButton);
        registerLinkButton = findViewById(R.id.registerLinkButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        emailPasswordUserLogin = EmailPasswordUserLogin.create(activity, firebaseAuth, eMailEditText, passwordEditText);

        // Google
        googleUserLogin.createGoogleRequest();

        // Create Facebook Request
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookUserLogin.handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Keyboard "Done" pressed
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    emailPasswordUserLogin.login();
                    handled = true;
                }
                return handled;
            }
        });

        // Button Login pressed
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailPasswordUserLogin.login();
            }
        });

        // Button Google+ pressed
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleUserLogin.login();
            }
        });

        // Button Facebook pressed
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookUserLogin.login();
            }
        });

        // Button Twitter pressed
        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterUserLogin.login();
            }
        });

        // link zur RegisterActivity
        registerLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Button um Passwort zurückzusetzen
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAdress = eMailEditText.getText().toString().trim();
                firebaseAuth.sendPasswordResetEmail(emailAdress).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity, getResources().getString(R.string.sendPasswordResetEmail), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

/*        // keine Anmeldung, wenn User eingeloggt ist
        if (firebaseUser != null) {
            db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(documentSnapshot -> {
                if(documentSnapshot.isSuccessful()) {
                    // user exists
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    // user registration not completed
                    startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                    finish();
                }
            });*/

/*            db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if(task.getResult().exists()) {
                    // user exists
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    // user registration not completed
                    startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                    finish();
                }
            });*/
/*            if (db.collection("users").document(firebaseUser.getUid()).get() != null) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                finish();
            }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Google Result
        googleUserLogin.result(requestCode, data);
        // Facebook Result
        facebookUserLogin.result(requestCode, resultCode, data, callbackManager);
    }
}
