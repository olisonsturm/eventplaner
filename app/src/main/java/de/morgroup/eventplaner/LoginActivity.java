/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package de.morgroup.eventplaner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    TextView registerLinkButton;
    EditText userNameEditText;
    EditText passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerLinkButton = findViewById(R.id.registerLinkButton);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        registerLinkButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                                      startActivity(intent);
                                                      finish();
                                                  }
                                              }
        );

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(view);
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void login(View view) {
        Model model = Model.getInstance();
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (model.checkLoginData(userName, password)) {
            Intent intent = new Intent(view.getContext(), EventUebersichtActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, R.string.loginInvalid, Toast.LENGTH_LONG).show();
        }
    }

}
