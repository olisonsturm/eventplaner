package de.morgroup.eventplaner.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.db.User;
import de.morgroup.eventplaner.util.ProfileImageFromFirebase;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private User user = new User();

    private DrawerLayout drawer;
    private ImageView headerPB;
    private TextView headerName, headerMail;
    private TextView accountFirstLastName, accountNick, accountEmail, accountMobile, accountAddress;
    private RelativeLayout editFirstLastName, editNick, editEmail, editMobile, editAddress;
    private Button btnAccountDelete;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);
        });

        user.setUid(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());

        btnAccountDelete = findViewById(R.id.account_delete);

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
        headerName.setText(user.getFirstname() + " " + user.getLastname());
        accountFirstLastName.setText(user.getFirstname() + " " + user.getLastname());
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
                        confirmEdit();

                        headerName.setText(cFirstname + " " + cLastname);
                        accountFirstLastName.setText(cFirstname + " " + cLastname);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Spitzname
        accountNick.setText(user.getNickname());
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
                        confirmEdit();

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
        accountMobile.setText(user.getMobile());
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
                        confirmEdit();

                        accountMobile.setText(cMobile);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Address
        accountAddress.setText(user.getAddress());
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
                        confirmEdit();

                        accountAddress.setText(cAddress);

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //Confirm Button - DB UPLOAD
        btnAccountDelete.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.account_delete) + "?")
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                        db.collection("users").document(firebaseUser.getUid())
                                .delete()
                                .addOnCompleteListener(aVoid -> {
                                    // DocumentSnapshot successfully deleted!
                                    firebaseUser.delete().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            finish();
                                        }
                                    });
                                });
                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

    }

    private void confirmEdit() {
        db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(user);
    }

}