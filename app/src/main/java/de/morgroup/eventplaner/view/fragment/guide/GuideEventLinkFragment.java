package de.morgroup.eventplaner.view.fragment.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventActivity;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;


public class GuideEventLinkFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_link, container, false);
        ButterKnife.bind(this, view);
        ((EventGuideActivity) getActivity()).setFinish(true);
        return view;
    }

    @OnClick(R.id.open_event)
    void onFinishPress() {
        event = ((EventGuideActivity) getActivity()).getEvent();

        Intent intent = new Intent(getContext(), EventActivity.class);
        Gson gson = new Gson();
        String itemGson = gson.toJson(event);
        intent.putExtra("event", itemGson);
        startActivity(intent);
    }

}