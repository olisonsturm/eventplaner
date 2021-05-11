package de.morgroup.eventplaner.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.db.User;

public class ConfirmActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDB = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private Button confirm;
    private ImageView headerPB;
    private TextView headerName, headerMail;
    private TextView accountFirstLastName, accountNick, accountEmail, accountMobile, accountAddress;
    private RelativeLayout editFirstLastName, editNick, editEmail, editMobile, editAddress;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        firebaseUser = firebaseAuth.getCurrentUser();

        confirm = findViewById(R.id.confirm);

        headerPB = findViewById(R.id.header_pb);
        headerName = findViewById(R.id.header_name);
        headerMail = findViewById(R.id.header_mail);

        accountFirstLastName = findViewById(R.id.account_first_last_name);
        accountNick = findViewById(R.id.account_nick);
        accountEmail = findViewById(R.id.account_email);
        accountMobile = findViewById(R.id.account_mobile);
        accountAddress = findViewById(R.id.account_address);

        editFirstLastName = findViewById(R.id.editFirstLastName);
        editNick = findViewById(R.id.editNick);
        editMobile = findViewById(R.id.editMobile);
        editAddress = findViewById(R.id.editAddress);

        //set Profile Image
        headerPB.setImageResource(R.drawable.img_placeholder);
        // -> ProfileImageFromFirebase image = new ProfileImageFromFirebase(headerPB);
        // -> image.execute(firebaseUser.getPhotoUrl().toString());

        //set Name
        headerName.setText(firebaseUser.getDisplayName());
        accountFirstLastName.setHint(getResources().getString(R.string.firstlastnameHint));
        editFirstLastName.setOnClickListener(task -> {
            LinearLayout view = new LinearLayout(this);
            EditText firstname = new EditText(this);
            EditText lastname = new EditText(this);
            firstname.setInputType(InputType.TYPE_CLASS_TEXT);
            lastname.setInputType(InputType.TYPE_CLASS_TEXT);
            view.setOrientation(LinearLayout.VERTICAL);
            firstname.setHint(getResources().getString(R.string.firstnameHint));
            lastname.setHint(getResources().getString(R.string.lastnameHint));
            view.addView(firstname);
            view.addView(lastname);
            float dpi = getResources().getDisplayMetrics().density;
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.firstAndLastName))
                    .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {

                        String cFirstname = firstname.getText().toString().toLowerCase().trim();
                        cFirstname = cFirstname.substring(0, 1).toUpperCase() + cFirstname.substring(1);
                        String cLastname = lastname.getText().toString().toLowerCase().trim();
                        cLastname = cLastname.substring(0, 1).toUpperCase() + cLastname.substring(1);
                        headerName.setText(cFirstname + " " + cLastname);
                        accountFirstLastName.setText(cFirstname + " " + cLastname);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Spitzname
        accountNick.setHint(getResources().getString(R.string.egHint));
        editNick.setOnClickListener(task -> {
            EditText view = new EditText(this);
            view.setHint(getResources().getString(R.string.nicknameHint));
            view.setInputType(InputType.TYPE_CLASS_TEXT);
            float dpi = getResources().getDisplayMetrics().density;
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.nickName))
                    .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {

                        String cNickname = view.getText().toString().trim();
                        accountNick.setText(cNickname);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Email
        headerMail.setText(firebaseUser.getEmail());
        accountEmail.setText(firebaseUser.getEmail());

        //set Mobile
        accountMobile.setHint(getResources().getString(R.string.mobileHint));
        editMobile.setOnClickListener(task -> {
            EditText view = new EditText(this);
            view.setHint(getResources().getString(R.string.mobileHint));
            view.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            float dpi = getResources().getDisplayMetrics().density;
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.mobile))
                    .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {

                        String cMobile = view.getText().toString().trim();
                        accountMobile.setText(cMobile);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Address
        accountAddress.setHint(getResources().getString(R.string.addressHint));
        editAddress.setOnClickListener(task -> {
            EditText view = new EditText(this);
            view.setHint(getResources().getString(R.string.addressHint));
            float dpi = getResources().getDisplayMetrics().density;
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.address))
                    .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {

                        String cAddress = view.getText().toString();
                        accountAddress.setText(cAddress);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        // the confirm button is saving the user data (calling method saveData)
        confirm.setOnClickListener(v -> {
            saveData();
        });

    }

    private void saveData() {

        // check that everything has been specified
        if (TextUtils.isEmpty(accountFirstLastName.getText().toString())) {
            return;
        }
        if (TextUtils.isEmpty(accountNick.getText().toString())) {
            return;
        }
        if (TextUtils.isEmpty(accountMobile.getText().toString())) {
            return;
        }
        if (TextUtils.isEmpty(accountAddress.getText().toString())) {
            return;
        }

        // getting the set data
        String uUid = firebaseUser.getUid();
        String uEmail = firebaseUser.getEmail();

        String[] uNameCache = accountFirstLastName.getText().toString().split(" ");
        String uFirstname = uNameCache[0];
        String uLastname = uNameCache[1];
        String uNickname = accountNick.getText().toString();
        String uMobile = accountMobile.getText().toString();
        String uAddress = accountAddress.getText().toString();

        // set user
        User user = new User();
        user.setUid(uUid);
        user.setFirstname(uFirstname);
        user.setLastname(uLastname);
        user.setNickname(uNickname);
        user.setEmail(uEmail);
        user.setMobile(uMobile);
        user.setAddress(uAddress);


        // profile image update
        if (firebaseUser.getPhotoUrl() != null) {
            String uPhotoUrl = firebaseUser.getPhotoUrl().toString();
            user.setPhotourl(uPhotoUrl);
        }

        // creating user document with the firebase auth uid as document name and with the user information
        userDB.set(user)
                .addOnSuccessListener(success -> {
                    // account created message
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.accountCreated), Toast.LENGTH_LONG);
                    // start the main activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // exception message
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                });

    }

}
