package de.morgroup.eventplaner.view.fragment.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.User;
import de.morgroup.eventplaner.view.activity.EventActivity;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;
import de.morgroup.eventplaner.view.adapter.EventMemberItemAdapter;
import de.morgroup.eventplaner.view.adapter.MainEventItemAdapter;


public class EventMemberFragment extends Fragment {

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
    private EventMemberItemAdapter memberItemAdapter;
    private List memberItemList;

    private Event event;

    private User owner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_member, container, false);

        event = ((EventActivity) getActivity()).getEvent();
        owner = ((EventActivity) getActivity()).getOwner();

        recyclerView = view.findViewById(R.id.recycler_view);

        memberItemList = new ArrayList<>();

        memberItemAdapter = new EventMemberItemAdapter(getContext(), memberItemList, event, firebaseUser);

        ConcatAdapter concatenatedAdapter = new ConcatAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            class CreateEventView extends RecyclerView.ViewHolder {
                LinearLayout layout;
                TextView name, rank;
                ImageView photo;

                public CreateEventView(View view) {
                    super(view);
                    layout = (LinearLayout) view.findViewById(R.id.member_item_layout);
                    layout.setClickable(false);
                    layout.setFocusable(false);
                    name = (TextView) view.findViewById(R.id.member_item_name);
                    rank = (TextView) view.findViewById(R.id.member_item_rank);
                    photo = (ImageView) view.findViewById(R.id.member_item_photo);

                    // set information
                    name.setText(owner.getFirstname() + " " + owner.getLastname());
                    rank.setText(getResources().getString(R.string.owner));


                    // loading event thumbnail url by using Glide library
                    if (owner.getPhotourl() != null) {
                        Glide.with(getContext())
                                .load(owner.getPhotourl())
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                                .into(photo);
                    } else {
                        Glide.with(getContext())
                                .load(R.drawable.img_placeholder)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                                .into(photo);
                    }

                }
            }

            @NonNull
            @NotNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.member_list_item, parent, false);
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
        }, memberItemAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(concatenatedAdapter);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listenerRegistration = db.collection("users").addSnapshotListener((documents, e) -> {
            // clear all event items for refresh
            memberItemList.clear();
            // catch errors (no permissions)
            if (e != null) {
                return;
            }
            for (QueryDocumentSnapshot snapshot : documents) {
                if (event.getMember().contains(snapshot.getId())) {
                    User user = snapshot.toObject(User.class);
                    // show the event member
                    if (!user.getUid().equals(event.getOwner())) {
                        memberItemList.add(user);
                    } else {
                        owner = user;
                    }

                }
            }
            Collections.sort(memberItemList);
            memberItemAdapter.notifyDataSetChanged();
        });
    }

}