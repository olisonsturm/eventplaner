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
import android.view.View;
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

        registerLinkButton = (TextView) findViewById(R.id.registerLinkButton);

        registerLinkButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                                      startActivity(intent);

                                                  }
                                              }
        );

        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model model = Model.getInstance();
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (model.checkLoginData(userName, password)) {
                    Intent intent = new Intent(view.getContext(), EventUebersichtActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.loginInvalid, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
