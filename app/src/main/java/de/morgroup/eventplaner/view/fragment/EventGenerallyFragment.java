package de.morgroup.eventplaner.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.adapter.EventItemAdapter;


public class EventGenerallyFragment extends Fragment {

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

    public EventGenerallyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_own_events, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // wrong only one event
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

            }
        });
    }

}