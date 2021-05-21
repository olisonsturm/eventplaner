package de.morgroup.eventplaner.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.FaceDetector;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.auth.EmailPasswordUserLogin;
import de.morgroup.eventplaner.auth.FacebookUserLogin;
import de.morgroup.eventplaner.auth.GoogleUserLogin;
import de.morgroup.eventplaner.auth.TwitterUserLogin;

public class LoginActivity extends Activity {

    // init
    private Activity activity = this;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @BindView(R.id.eMailEditText)
    EditText eMailEditText;
    @BindView(R.id.passwordEditText)
    EditText passwordEditText;
    @BindView(R.id.loginButton)
    Button loginButton;

    // auth
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
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Email Password
        emailPasswordUserLogin = EmailPasswordUserLogin.create(activity, firebaseAuth, eMailEditText, passwordEditText);

        // Google
        googleUserLogin.createGoogleRequest();

        // Create Facebook Request
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
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

    }

    @OnClick(R.id.passwordEditText)
    void onEnterPress() {
        emailPasswordUserLogin.login();
    }

    @OnClick(R.id.loginButton)
    void onLoginClick() {
        emailPasswordUserLogin.login();
    }

    @OnClick(R.id.googleLoginButton)
    void onGoogleLoginClick() {
        googleUserLogin.login();
    }

    @OnClick(R.id.facebookLoginButton)
    void onFacebookLoginClick() {
        facebookUserLogin.login();
    }

    @OnClick(R.id.twitterLoginButton)
    void onTwitterLoginClick() {
        twitterUserLogin.login();
    }

    @OnClick(R.id.registerLinkButton)
    void onRegisterLinkClick() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.forgotPasswordTextView)
    void onForgotPasswordClick() {
        String email = eMailEditText.getText().toString().trim();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.sendPasswordResetEmail), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Google Result
        googleUserLogin.result(requestCode, data);
        // Facebook Result
        facebookUserLogin.result(requestCode, resultCode, data, callbackManager);
    }
}
