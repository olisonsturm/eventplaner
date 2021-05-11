package de.morgroup.eventplaner.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.HashMap;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.db.User;
import de.morgroup.eventplaner.util.ProfileImageFromFirebase;

public class ConfirmActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    private User user;

    private DrawerLayout drawer;
    private ImageView headerPB;
    private TextView headerName, headerMail;
    private TextView accountFirstLastName, accountNick, accountEmail, accountMobile, accountAddress;
    private RelativeLayout editFirstLastName, editNick, editEmail, editMobile, editAddress;
    private Button confirm;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        user = User.getInstance();

        user.setUid(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());

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
        ProfileImageFromFirebase image = new ProfileImageFromFirebase(headerPB);
        image.execute(firebaseUser.getPhotoUrl().toString());

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

                        user.setFirstname(cFirstname);
                        user.setLastname(cLastname);

                        headerName.setText(cFirstname + " " + cLastname);
                        accountFirstLastName.setText(cFirstname + " " + cLastname);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Spitzname
        String nickHint = firebaseUser.getDisplayName();
        if (firebaseUser.getDisplayName().contains(" ")) {
            nickHint = nickHint.split(" ")[0];
        } else {
            if (nickHint == null || nickHint.isEmpty()) {
                return;
            }
            nickHint = firebaseUser.getDisplayName().substring(0, 3).toLowerCase();
            nickHint = nickHint.substring(0, 1).toUpperCase() + nickHint.substring(1);
        }
        accountNick.setHint(getResources().getString(R.string.egHint) + " " + nickHint);
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

                        user.setNickname(cNickname);

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

                        user.setMobile(cMobile);

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

                        user.setAddress(cAddress);

                        accountAddress.setText(cAddress);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //Confirm Button - DB UPLOAD
        confirm.setOnClickListener(v -> {
            confirmClick(v);
        });

    }

    private void confirmClick(View v) {
        if (TextUtils.isEmpty(user.getFirstname())) {
            return;
        }
        if (TextUtils.isEmpty(user.getLastname())) {
            return;
        }
        if (TextUtils.isEmpty(user.getNickname())) {
            return;
        }
        if (TextUtils.isEmpty(user.getMobile())) {
            return;
        }
        if (TextUtils.isEmpty(user.getAddress())) {
            return;
        }
        db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar.make(v, getResources().getString(R.string.accountCreated), Snackbar.LENGTH_LONG);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

}
