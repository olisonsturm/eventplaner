package de.morgroup.eventplaner.view.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import de.morgroup.eventplaner.view.activity.decoration.MainDateHeaderItemDecoration;
import de.morgroup.eventplaner.view.adapter.EventItemAdapter;

public class MainAllEventsFragment extends Fragment implements MainDateHeaderItemDecoration.StickyHeaderInterface {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListenerRegistration listenerRegistration;

    private RecyclerView recyclerView;
    private EventItemAdapter eventItemAdapter;
    private List eventItemList;

    public MainAllEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        //ADD NEW EVENT
        DocumentReference ref = db.collection("events").document();
        Event event = new Event();
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-1998");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event.setDay(Timestamp.now());
        event.setId(ref.getId());

        event.setMember(new ArrayList<String>(Arrays.asList("O8jIznrIC1Uq3v0FupPM1KDDVSJ2")));
        event.setName("Test2");
        event.setOwner("O8jIznrIC1Uq3v0FupPM1KDDVSJ2");
        event.setTime("11 Uhr");
        ref.set(event);
        */


        View view = inflater.inflate(R.layout.fragment_main_all_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        eventItemList = new ArrayList<>();
        // TODO: OWNER FIX
        eventItemAdapter = new EventItemAdapter(getContext(), eventItemList, false, firebaseUser);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(eventItemAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MainDateHeaderItemDecoration(recyclerView, eventItemAdapter));

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        /*
         *  TODO: Leider sind keine OR abfragen, also ob user in member or user is owner möglich!
         */
        // getting data by listener
        listenerRegistration = db.collection("events").whereArrayContains("member", firebaseUser.getUid()).addSnapshotListener((snapshotsMember, eMember) -> {
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


    // ?????
    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return 0;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {

    }

    @Override
    public boolean isHeader(int itemPosition) {
        return false;
    }
}