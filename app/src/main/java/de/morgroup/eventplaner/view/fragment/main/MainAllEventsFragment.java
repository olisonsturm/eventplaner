package de.morgroup.eventplaner.view.fragment.main;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.adapter.MainEventItemAdapter;

public class MainAllEventsFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListenerRegistration listenerRegistration;

    private RecyclerView recyclerView;
    private MainEventItemAdapter eventItemAdapter;
    private List eventItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_all_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        eventItemList = new ArrayList<>();

        eventItemAdapter = new MainEventItemAdapter(getContext(), eventItemList, firebaseUser);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(eventItemAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        // getting data by listener
        listenerRegistration = db.collection("events")
                .whereArrayContains("member", firebaseUser.getUid())
                .addSnapshotListener((snapshotsMember, eMember) -> {
            // clear all event items for refresh
            eventItemList.clear();
            // catch errors (no permissions)
            if (eMember != null) {
                return;
            }
            Event eventMember;
            // getting data and update
            for (QueryDocumentSnapshot document : snapshotsMember) {
                // receive the user object from db
                eventMember = document.toObject(Event.class);
                // show the event
                eventItemList.add(eventMember);
            }
            Collections.sort(eventItemList);
            eventItemAdapter.notifyDataSetChanged();
        });
    }
}