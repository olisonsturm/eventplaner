package de.morgroup.eventplaner.view.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.User;
import de.morgroup.eventplaner.model.Voting;


public class CreateVotingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference votingDB;

    private ListenerRegistration listenerRegistration;
    private Calendar calendar = Calendar.getInstance();

    private Voting voting;

    @BindView(R.id.question)
    EditText question;
    @BindView(R.id.end_time)
    EditText endTime;
    @BindViews({R.id.o1, R.id.o2, R.id.o3, R.id.o4, R.id.o5, R.id.o6, R.id.o7, R.id.o8, R.id.o9, R.id.o10})
    EditText options[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_voting);
        ButterKnife.bind(this);

        voting = new Voting();

    }

    @OnClick(R.id.end_time)
    void endTimePress() {
        // Date Picker
        Calendar currentDate = Calendar.getInstance();
        int y = currentDate.get(Calendar.YEAR);
        int d = currentDate.get(Calendar.MONTH);
        int m = currentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Date date = new GregorianCalendar(year, month - 1, dayOfMonth).getTime();
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            timePicker(dateFormat.format(date));
        }, y, d, m);
        datePicker.getDatePicker().setMinDate(new Date().getTime() + (1000 * 60 * 60 * 24)); // plus one day
        datePicker.show();
    }

    // Time Picker
    private void timePicker(String date) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            endTime.setText(date + " (" + hourOfDay + ":" + minute + " Uhr)");
        }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    @OnClick(R.id.add_answer)
    void onAddAnswerPress() {
        for (EditText option : options) {
            // TODO: automatisches einblenden neuer answer text fields
            if (option.getVisibility() == View.GONE || option.getVisibility() == View.INVISIBLE) {
                option.setVisibility(View.VISIBLE);
                return;
            }
        }
    }

    @OnClick(R.id.close)
    void onClosePress() {
        finish();
    }

    @OnClick(R.id.create_voting)
    void onCreatePress() {
        saveData();
    }

    private void saveData() {

        // check that everything has been specified
        if (TextUtils.isEmpty(question.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(endTime.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(options[0].getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(options[1].getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show();
            return;
        }

        votingDB = db.collection("events").document(getEventId()).collection("voting").document();

        voting.setId(votingDB.getId());
        voting.setName(question.getText().toString());

        String datetime = endTime.getText().toString().replace("(", "").replace(" Uhr)", "");
        Date date = null;
        try {
            date = new SimpleDateFormat("dd.MM.yy hh:mm").parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        voting.setEndTime(new Timestamp(date));

        ArrayList o = new ArrayList<>();
        for (EditText option : options) {
            if (!TextUtils.isEmpty(option.getText().toString())) {
                o.add(option.getText().toString());
            }
        }
        voting.setOptions(o);

        // creating voting document
        votingDB.set(voting)
                .addOnSuccessListener(success -> {
                    // voting created
                    Toast.makeText(getApplicationContext(), "Voting created", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // exception message
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

    private String getEventId() {
        if (getIntent().hasExtra("eventId")) {
            return getIntent().getStringExtra("eventId");
        }
        return null;
    }

}