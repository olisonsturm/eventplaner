package de.morgroup.eventplaner.view.fragment.guide;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.adapter.EventItemAdapter;


public class GuideEventTimeFragment extends Fragment {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GuideEventTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_time, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.NextCreateEvent)
    void onNextPagePress() {
        Navigation.findNavController(getView()).navigate(R.id.action_EventTimeFragment_to_EventLinkFragment);
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