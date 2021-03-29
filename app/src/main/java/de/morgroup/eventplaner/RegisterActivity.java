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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class RegisterActivity extends Activity {

    TextView loginLinkButton;
    EditText userNameEditText;
    EditText eMailEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginLinkButton = findViewById(R.id.loginLinkButton);
        userNameEditText = findViewById(R.id.userNameEditText);
        eMailEditText = findViewById(R.id.eMailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        registerButton = findViewById(R.id.registerButton);

        loginLinkButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                      startActivity(intent);
                                                      finish();
                                                  }
                                              }
        );

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(view);
            }
        });

        passwordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    register(view);
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void register(View view) {
        Model model = Model.getInstance();
        String userName = userNameEditText.getText().toString();
        String eMail = eMailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();
        switch (model.checkRegisterData(userName, eMail, password, passwordAgain)) {
            case 0:
                Intent intent = new Intent(view.getContext(), EventUebersichtActivity.class);
                startActivity(intent);
                finish();
            case 1:
                Toast.makeText(RegisterActivity.this, R.string.registerErrorUserName, Toast.LENGTH_LONG).show();
            case 2:
                Toast.makeText(RegisterActivity.this, R.string.registerErrorPassword, Toast.LENGTH_LONG).show();
            default:
                Toast.makeText(RegisterActivity.this, R.string.registerInvalid, Toast.LENGTH_LONG).show();
        }
    }
}
