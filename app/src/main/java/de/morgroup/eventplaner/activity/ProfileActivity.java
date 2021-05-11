package de.morgroup.eventplaner.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.db.User;
import de.morgroup.eventplaner.util.ProfileImageFB;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser();
    private DocumentReference userDB = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private ListenerRegistration listenerRegistration;

    private Button btnAccountDelete;
    private ImageView headerPB;
    private TextView headerName, headerMail;
    private TextView accountFirstLastName, accountNick, accountEmail, accountMobile, accountAddress;
    private RelativeLayout editFirstLastName, editNick, editEmail, editMobile, editAddress;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

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

        // set name
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
                        if ((firstname.getText() != null) && (lastname.getText() != null)) {
                            if (firstname.getText().toString().length() > 0 && lastname.getText().toString().length() > 0) {
                                String cFirstname = firstname.getText().toString().toLowerCase().replace(" ", "");
                                cFirstname = cFirstname.substring(0, 1).toUpperCase() + cFirstname.substring(1);
                                String cLastname = lastname.getText().toString().toLowerCase().replace(" ", "");
                                cLastname = cLastname.substring(0, 1).toUpperCase() + cLastname.substring(1);
                                headerName.setText(cFirstname + " " + cLastname);
                                accountFirstLastName.setText(cFirstname + " " + cLastname);
                                updateData("firstname", cFirstname);
                                updateData("lastname", cLastname);
                            }
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Spitzname
        editNick.setOnClickListener(task -> {
            EditText view = new EditText(this);
            view.setHint(getResources().getString(R.string.nicknameHint));
            view.setInputType(InputType.TYPE_CLASS_TEXT);
            float dpi = getResources().getDisplayMetrics().density;
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.nickName))
                    .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                        if (view.getText() != null) {
                            if (view.getText().toString().length() > 0) {
                                String cNickname = view.getText().toString().trim();
                                accountNick.setText(cNickname);
                                updateData("nickname", cNickname);
                            }
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Email verifiziert!

        //set Mobile
        editMobile.setOnClickListener(task -> {
            EditText view = new EditText(this);
            view.setHint(getResources().getString(R.string.mobileHint));
            view.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            float dpi = getResources().getDisplayMetrics().density;
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.mobile))
                    .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                        if (view.getText() != null) {
                            if (view.getText().toString().length() > 0) {
                                String cMobile = view.getText().toString().trim();
                                accountMobile.setText(cMobile);
                                updateData("mobile", cMobile);
                            }
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        //set Address
        editAddress.setOnClickListener(task -> {
            EditText view = new EditText(this);
            view.setHint(getResources().getString(R.string.addressHint));
            float dpi = getResources().getDisplayMetrics().density;
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.address))
                    .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                        if (view.getText() != null) {
                            if (view.getText().toString().length() > 0) {
                                String cAddress = view.getText().toString();
                                accountAddress.setText(cAddress);
                                updateData("address", cAddress);
                            }
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

        // account delete (drop auth and doc)
        btnAccountDelete.setOnClickListener(v -> {
            FirebaseAuth.getInstance().getCurrentUser().reload();
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.account_delete) + "?")
                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {

                        userDB.delete().addOnSuccessListener(task -> {
                            // DocumentSnapshot successfully deleted
                            FirebaseAuth.getInstance().getCurrentUser().delete()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // back to login activity
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(),task1.getException().getMessage(),Toast.LENGTH_LONG);
                                        }
                                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG));
                        });

                    })
                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                    .create();
            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        });

    }

    private void updateData(String field, String value) {

        // update user document with new user data
        userDB.update(field, value)
                .addOnFailureListener(e -> {
                    // exception message
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // getting data by listener
        listenerRegistration = userDB.addSnapshotListener((documentSnapshot, e) -> {
            // preventing errors
            if (e != null) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                return;
            }

            // getting data and update
            if (documentSnapshot.exists()) {

                // receive the user object from db
                User user = documentSnapshot.toObject(User.class);

                // cache the data
                String firstname = user.getFirstname();
                String lastname = user.getLastname();
                String nickname = user.getNickname();
                String email = user.getEmail();
                String mobile = user.getMobile();
                String address = user.getAddress();

                // show the data
                new ProfileImageFB(headerPB).execute(user.getPhotourl());
                headerName.setText(firstname + " " + lastname);
                accountFirstLastName.setText(firstname + " " + lastname);
                headerMail.setText(email);
                accountEmail.setText(email);
                accountNick.setText(nickname);
                accountMobile.setText(mobile);
                accountAddress.setText(address);

            }

        });
    }
}