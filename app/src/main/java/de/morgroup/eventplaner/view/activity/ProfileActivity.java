package de.morgroup.eventplaner.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.MimeTypeFilter;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.User;
import de.morgroup.eventplaner.util.ProfileImageFB;

@SuppressLint("NonConstantResourceId")
public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDB = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    // Profile Picture
    private StorageReference storage;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private ListenerRegistration listenerRegistration;

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

    @BindView(R.id.verification_email)
    ImageView checkEmailVerification;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        storage = FirebaseStorage.getInstance().getReference("uploads");

        if (firebaseUser.isEmailVerified()) {
            checkEmailVerification.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_verification_true));
        } else {
            checkEmailVerification.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_verification_false));
        }

    }

    @OnClick(R.id.header_pb)
    void onPictureClick() {
        openImage();
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
                            accountNick.setText(cNickname);
                            updateData("nickname", cNickname);
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
                            accountMobile.setText(cMobile);
                            updateData("mobile", cMobile);
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
//            EditText view = new EditText(this);
//            view.setHint(getResources().getString(R.string.addressHint));
//            float dpi = getResources().getDisplayMetrics().density;
//            AlertDialog alertDialog = new AlertDialog.Builder(this)
//                    .setTitle(getResources().getString(R.string.address))
//                    .setView(view, (int) (20 * dpi), (int) (10 * dpi), (int) (20 * dpi), (int) (0 * dpi))
//                    .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
//                        if (view.getText() != null) {
//                            if (view.getText().toString().length() > 0) {
//                                String cAddress = view.getText().toString();
//                                accountAddress.setText(cAddress);
//                                updateData("address", cAddress);
//                            }
//                        }
//                    })
//                    .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
//                    .create();
//            alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
//            alertDialog.show();
//    }

    @OnClick(R.id.account_delete)
    void onAccountDeletePress() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.account_delete) + "?")
                .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                            firebaseUser.reload();
                            db.collection("users").document(firebaseUser.getUid()).delete().addOnCompleteListener(task -> {
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                firebaseUser.reload();
                                firebaseUser.delete();
                                finish();
                            });
                            if (firebaseUser != null) {
                                firebaseUser.delete();
                            }
                            finish();
                        }
                )
                .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                .create();
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseAuth.AuthStateListener firebaseAuthListener = auth -> {
            firebaseUser.reload();
        };
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        // getting data by listener
        listenerRegistration = userDB.addSnapshotListener((documentSnapshot, e) -> {
            // preventing errors
            if (e != null) {
                return;
            }
            // getting data and update
            if (documentSnapshot.exists()) {

                // receive the user object from db
                User user = documentSnapshot.toObject(User.class);

                // cache the data
                assert user != null;
                String firstname = user.getFirstname();
                String lastname = user.getLastname();
                String nickname = user.getNickname();
                String email = user.getEmail();
                String mobile = user.getMobile();
                //String[] addressCache = user.getAddress().split("_");
                //String address = addressCache[0] + " " + addressCache[1] + ", " + addressCache[2];

                // show the data
                new ProfileImageFB(headerPB).execute(user.getPhotourl());
                headerName.setText(firstname + " " + lastname);
                accountFirstLastName.setText(firstname + " " + lastname);
                headerMail.setText(email);
                accountEmail.setText(email);
                accountNick.setText(nickname);
                accountMobile.setText(mobile);
                //accountAddress.setText(address);
            }
        });
    }

    private void updateData(String field, String value) {
        // update user document with new user data
        userDB.update(field, value)
                .addOnFailureListener(e -> {
                });
    }

    // Profile Change Picture Methodes
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplication().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getApplicationContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            final StorageReference fileReference = storage.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.getFile(imageUri);
            uploadTask.continueWith(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCanceledListener(new OnCompleteListener<>());
        }
    }

}