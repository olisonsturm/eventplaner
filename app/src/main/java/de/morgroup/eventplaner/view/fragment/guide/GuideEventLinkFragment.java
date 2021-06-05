package de.morgroup.eventplaner.view.fragment.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventActivity;
import de.morgroup.eventplaner.view.adapter.EventItemAdapter;


public class GuideEventLinkFragment extends Fragment {


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Event event;


    public GuideEventLinkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_link, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.NextCreateEvent)
    void onNextPagePress() {
        // -------------------------------------------------------------------------- ONLY TEST
        event = new Event();
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-1998");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event.setDay(Timestamp.now());
        event.setId("dfsdfdfsgdfgdfghdfg");
        event.setMember(new ArrayList<String>(Arrays.asList("O8jIznrIC1Uq3v0FupPM1KDDVSJ2")));
        event.setName("Test2");
        event.setOwner("O8jIznrIC1Uq3v0FupPM1KDDVSJ2");
        event.setTime("11 Uhr");
        // --------------------------------------------------------------------------
        Intent intent = new Intent(getContext(), EventActivity.class);
        Gson gson = new Gson();
        String itemGson = gson.toJson(event);
        intent.putExtra("event", itemGson);
        startActivity(intent);
    }

    @OnClick(R.id.CloseCreateEvent)
    void onClosePress() {
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

}