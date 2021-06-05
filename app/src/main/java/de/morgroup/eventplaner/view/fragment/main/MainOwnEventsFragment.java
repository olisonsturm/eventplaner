package de.morgroup.eventplaner.view.fragment.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;
import de.morgroup.eventplaner.view.adapter.EventItemAdapter;


public class MainOwnEventsFragment extends Fragment {

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
    private EventItemAdapter eventItemAdapter;
    private List eventItemList;

    public MainOwnEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_own_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        eventItemList = new ArrayList<>();
        eventItemAdapter = new EventItemAdapter(getContext(), eventItemList, true, firebaseUser);
        ConcatAdapter concatenatedAdapter = new ConcatAdapter(eventItemAdapter, new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            class CreateEventView extends RecyclerView.ViewHolder {
                CardView create;
                public CreateEventView(View view) {
                    super(view);
                    create = (CardView) view.findViewById(R.id.create_event);
                    create.setOnClickListener(v -> {
                        startActivity(new Intent(getContext(), EventGuideActivity.class));
                    });
                }
            }
            @NonNull
            @NotNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_create_item, parent, false);
                return new CreateEventView(view);
            }
            @Override
            public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

            }
            @Override
            public int getItemCount() {
                int create = 1;
                return create;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(concatenatedAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listenerRegistration = db.collection("events").whereEqualTo("owner", firebaseUser.getUid()).addSnapshotListener((snapshotsOwner, eOwner) -> {
            // clear all event items for refresh
            eventItemList.clear();
            // catch errors (no permissions)
            if (eOwner != null) {
                return;
            }
            Event eventOwner;
            // getting data and update
            for (QueryDocumentSnapshot document : snapshotsOwner) {
                // receive the user object from db
                eventOwner = document.toObject(Event.class);
                // show the event
                eventItemList.add(eventOwner);
            }
            Collections.sort(eventItemList);
            eventItemAdapter.notifyDataSetChanged();
        });
    }

}