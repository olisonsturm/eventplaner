package de.morgroup.eventplaner.view.fragment.event;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.User;
import de.morgroup.eventplaner.view.activity.EventActivity;


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
    private DocumentReference eventDB;

    private ListenerRegistration listenerRegistration;

    private Event event;

    @BindView(R.id.event_thumbnail)
    ImageView thumbnail;
    @BindView(R.id.event_description)
    EditText description;
    @BindView(R.id.event_edit_description)
    TextView editDescriptionButton;
    @BindView(R.id.event_edit_place)
    TextView editPlaceButton;

    @BindView(R.id.event_map)
    MapView map = null;
    private IMapController mapController;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        event = ((EventActivity) getActivity()).getEvent();

        eventDB = db.collection("events").document(event.getId());

        View view = inflater.inflate(R.layout.fragment_event_generally, container, false);
        ButterKnife.bind(this, view);

        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(12);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return view;
    }

    @OnClick(R.id.event_edit_description)
    void onDescriptionEditClick() {
        if (!description.isEnabled()) {
            editDescriptionButton.setBackgroundResource(R.drawable.ic_check);
            description.setEnabled(true);
        } else {
            editDescriptionButton.setBackgroundResource(R.drawable.ic_profile);
            description.setEnabled(false);
            String desc = description.getText().toString();
            System.out.println(desc);
            updateEvent("description", description.getText().toString());
        }
    }

    @OnClick(R.id.event_edit_place)
    void onPlaceEditClick() {
        Toast.makeText(getContext(),"not included", Toast.LENGTH_SHORT).show();
    }

    public void updateEvent(String field, String value) {
        eventDB.update(field, value);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (event.getOwner().equals(firebaseUser.getUid())) {
            // OWNER

            // buttons
            editDescriptionButton.setVisibility(View.VISIBLE);
            editPlaceButton.setVisibility(View.VISIBLE);

            // hint
            description.setHint(R.string.describe_your_event);
        } else {
            // MEMBER

            // hint
            db.collection("users").document(event.getOwner()).addSnapshotListener((documentSnapshot, e) -> {
                // preventing errors
                if (e != null) return;
                // getting data and update
                if (documentSnapshot.exists()) {
                    // receive the user object from db
                    User user = documentSnapshot.toObject(User.class);
                    // show the data
                    String default_description = getResources().getString(R.string.default_description).replace("%owner", user.getFirstname() + " " + user.getLastname());
                    description.setHint(default_description);
                }
            });
        }

        // getting data by listener
        listenerRegistration = eventDB.addSnapshotListener((documentSnapshot, e) -> {
            // preventing errors
            if (e != null) {
                return;
            }
            // getting data and update
            if (documentSnapshot.exists()) {
                // receive the user object from db
                Event event = documentSnapshot.toObject(Event.class);
                // show the data
                if (event.getThumbnailUrl() != null) {
                    Glide.with(getContext())
                            .load(event.getThumbnailUrl())
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(thumbnail);
                } else {
                    thumbnail.setImageResource(R.drawable.img_placeholder_event);
                }
                if (!TextUtils.isEmpty(event.getDescription())) {
                    description.setText(event.getDescription());
                }
            }
        });
    }
}