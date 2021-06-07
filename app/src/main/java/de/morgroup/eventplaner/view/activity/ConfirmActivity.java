package de.morgroup.eventplaner.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.User;

@SuppressLint("NonConstantResourceId")
public class ConfirmActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDB = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private User user;

    @BindView(R.id.header_pb)
    ImageView headerPB;
    @BindView(R.id.header_name)
    TextView headerName;
    @BindView(R.id.header_mail)
    TextView headerMail;
    @BindView(R.id.account_first_last_name)
    TextView accountFirstLastName;
    @BindView(R.id.account_nick)
    TextView accountNick;
    @BindView(R.id.account_email)
    TextView accountEmail;
    @BindView(R.id.account_mobile)
    TextView accountMobile;
//    @BindView(R.id.account_address)
//    TextView accountAddress;

    @BindView(R.id.verification_email)
    ImageView checkEmailVerification;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        user = new User();

        user.setUid(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());

        // set PB
        if (user.getPhotourl() != null) {
            Glide.with(getApplicationContext())
                    .load(user.getPhotourl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(headerPB);
        } else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.img_placeholder)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(headerPB);
        }

        //set Name
        headerName.setText(firebaseUser.getDisplayName());
        accountFirstLastName.setHint(getResources().getString(R.string.firstlastnameHint));

        //set Email
        headerMail.setText(firebaseUser.getEmail());
        accountEmail.setText(firebaseUser.getEmail());

        if (firebaseUser.isEmailVerified()) {
            checkEmailVerification.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_verification_true));
        } else {
            checkEmailVerification.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_verification_false));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser.reload();
    }

    @OnClick(R.id.header_pb)
    void onPictureClick() {
        startActivity(new Intent(getApplicationContext(), CropActivity.class));
    }

    @OnClick(R.id.editFirstLastName)
    void onFirstLastNamePress() {
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
        @SuppressLint("RestrictedApi") AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.firstAndLastName))
                .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                    if ((firstname.getText() != null) && (lastname.getText() != null)) {
                        if (firstname.getText().toString().length() > 0 && lastname.getText().toString().length() > 0) {
                            String cFirstname = firstname.getText().toString().toLowerCase().replace(" ", "");
                            cFirstname = cFirstname.substring(0, 1).toUpperCase() + cFirstname.substring(1);
                            String cLastname = lastname.getText().toString().toLowerCase().replace(" ", "");
                            cLastname = cLastname.substring(0, 1).toUpperCase() + cLastname.substring(1);
                            user.setFirstname(cFirstname);
                            user.setLastname(cLastname);
                            headerName.setText(cFirstname + " " + cLastname);
                            accountFirstLastName.setText(cFirstname + " " + cLastname);
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                .create();
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
        alertDialog.show();
    }

    @OnClick(R.id.editNick)
    void onNickPress() {
        EditText view = new EditText(this);
        view.setHint(getResources().getString(R.string.nicknameHint));
        view.setInputType(InputType.TYPE_CLASS_TEXT);
        float dpi = getResources().getDisplayMetrics().density;
        @SuppressLint("RestrictedApi") AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.nickName))
                .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                    if (view.getText() != null) {
                        if (view.getText().toString().length() > 0) {
                            String cNickname = view.getText().toString().replace(" ", "");
                            user.setNickname(cNickname);
                            accountNick.setText(cNickname);
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                .create();
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
        alertDialog.show();
    }

    @OnClick(R.id.editMail)
    void onMailPress() {
        firebaseUser.reload();
        if (firebaseUser.isEmailVerified()) {
            checkEmailVerification.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_verification_true));
            Toast.makeText(getApplicationContext(), "Deine E-mail-Adresse ist bestätigt", Toast.LENGTH_LONG).show();
        } else {
            checkEmailVerification.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_verification_false));
            Toast.makeText(getApplicationContext(), "Bitte bestätige deine E-mail-Adresse", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.editMobile)
    void onMobilePress() {
        EditText view = new EditText(this);
        view.setHint(getResources().getString(R.string.mobileHint));
        view.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        float dpi = getResources().getDisplayMetrics().density;
        @SuppressLint("RestrictedApi") AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.mobile))
                .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
                .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                    if (view.getText() != null) {
                        if (view.getText().toString().length() > 0) {
                            String cMobile = view.getText().toString().replace(" ", "");
                            user.setMobile(cMobile);
                            accountMobile.setText(cMobile);
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                .create();
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
        alertDialog.show();
    }

//    @OnClick(R.id.editAddress)
//    void onAddressPress() {
//        LinearLayout view = new LinearLayout(this);
//        view.setOrientation(LinearLayout.VERTICAL);
//
//        String[] hint = getResources().getString(R.string.addressHint).replaceAll(",", "").split(" ");
//
//        EditText street = new EditText(this);
//        street.setInputType(InputType.TYPE_CLASS_TEXT);
//        street.setHint(hint[0]);
//
//        EditText number = new EditText(this);
//        number.setInputType(InputType.TYPE_CLASS_NUMBER);
//        number.setHint(hint[1]);
//
//        EditText pc = new EditText(this);
//        pc.setInputType(InputType.TYPE_CLASS_NUMBER);
//        pc.setHint(hint[2]);
//
//        view.addView(street);
//        view.addView(number);
//        view.addView(pc);
//        float dpi = getResources().getDisplayMetrics().density;
//        @SuppressLint("RestrictedApi") AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .setTitle(getResources().getString(R.string.address))
//                .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
//                .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
//                    if ((street.getText() != null) && (number.getText() != null) && (pc.getText() != null)) {
//                        if (street.getText().toString().length() > 0 && number.getText().toString().length() > 0 && pc.getText().toString().length() > 0) {
//                            if (street.getText().toString().contains("_") || number.getText().toString().contains("_") || number.getText().toString().contains("_")) {
//                                accountAddress.setError("_");
//                                return;
//                            }
//                            String cStreet = street.getText().toString();
//                            String cNumber = number.getText().toString();
//                            String cPc = pc.getText().toString();
//                            user.setAddress(cStreet + "_" + cNumber + "_" + cPc);
//                            accountAddress.setText(cStreet + " " + cNumber + ", " + cPc);
//                        }
//                    }
//                })
//                .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
//                .create();
//        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
//        alertDialog.show();
//    }

    @OnClick(R.id.confirm)
    void onConfirmPress() {
        // the confirm button is saving the user data (calling method saveData)
        saveData();
    }

    private void saveData() {

        // check that everything has been specified
        if (TextUtils.isEmpty(accountFirstLastName.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(accountNick.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(accountMobile.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show();
            return;
        }

//        if (TextUtils.isEmpty(accountAddress.getText().toString())) {
//            return;
//        }

        // creating user document with the firebase auth uid as document name and with the user information
        userDB.set(user)
                .addOnSuccessListener(success -> {
                    // account created message
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.accountCreated), Toast.LENGTH_LONG).show();
                    // start the main activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // exception message
                });

    }

}
