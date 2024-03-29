package de.morgroup.eventplaner.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.Voting;
import de.morgroup.eventplaner.view.adapter.AnswerItemAdapter;

public class VotingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference votingDB;

    private ListenerRegistration listenerRegistration;

    private Voting voting;
    private Event event;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.question)
    TextView question;

    @BindView(R.id.delete_voting)
    FloatingActionButton delete;

    private AnswerItemAdapter adapter;
    private List options;
    private int votesTotal;
    private ArrayList<ArrayList<String>> votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        ButterKnife.bind(this);

        voting = getIncomingVotingIntent();
        event = getIncomingEventIntent();

        votingDB = db.collection("events").document(event.getId()).collection("voting").document(voting.getId());

        question.setText(voting.getName());

        options = voting.getOptions();

        votes = new ArrayList<>();

        adapter = new AnswerItemAdapter(getApplicationContext(), options, votingDB);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.suppressLayout(true);

        if (event.getOwner().equals(firebaseUser.getUid())) {
            delete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerRegistration = db.collection("events").document(event.getId()).collection("voting").document(voting.getId()).addSnapshotListener((document, e) -> {
            // clear all event items for refresh
            options.clear();
            // catch errors (no permissions)
            if (e != null) {
                return;
            }
            if (document.exists()) {
                // receive the user object from db
                voting = document.toObject(Voting.class);
                // show the event
                options = voting.getOptions();
            }
            adapter.notifyDataSetChanged();
        });
    }

    @OnClick(R.id.close_voting)
    void onClosePress() {
        finish();
    }

    @OnClick(R.id.delete_voting)
    void onDeletePress() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_voting_löschen))
                .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                            // delete event
                            db.collection("events").document(event.getId()).collection("voting").document(voting.getId()).delete();
                            finish();
                        }
                )
                .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                .create();
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
        alertDialog.show();
    }

    private Voting getIncomingVotingIntent() {
        if (getIntent().hasExtra("voting")) {
            return (Voting) new Gson().fromJson(getIntent().getStringExtra("voting"), Voting.class);
        }
        return null;
    }

    private Event getIncomingEventIntent() {
        if (getIntent().hasExtra("event")) {
            return (Event) new Gson().fromJson(getIntent().getStringExtra("event"), Event.class);
        }
        return null;
    }

}
