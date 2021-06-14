package de.morgroup.eventplaner.view.fragment.event;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.Voting;
import de.morgroup.eventplaner.view.activity.EventActivity;
import de.morgroup.eventplaner.view.adapter.EventVotingItemAdapter;


public class EventVotingFragment extends Fragment {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListenerRegistration listenerRegistration;

    private RecyclerView recyclerView;
    private EventVotingItemAdapter votingItemAdapter;
    private List votingItemList;

    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_voting, container, false);

        event = ((EventActivity) getActivity()).getEvent();

        recyclerView = view.findViewById(R.id.recycler_view);

        votingItemList = new ArrayList<>();

        votingItemAdapter = new EventVotingItemAdapter(getContext(), votingItemList, event);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setAdapter(votingItemAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listenerRegistration = db.collection("events").document(event.getId()).collection("voting").addSnapshotListener((snapshots, e) -> {
            // clear all event items for refresh
            votingItemList.clear();
            // catch errors (no permissions)
            if (e != null) {
                return;
            }
            Voting voting;
            // getting data and update
            for (QueryDocumentSnapshot document : snapshots) {
                // receive the user object from db
                voting = document.toObject(Voting.class);
                // show the event
                votingItemList.add(voting);
            }
            //Collections.sort(votingItemList);
            votingItemAdapter.notifyDataSetChanged();
        });
    }

}