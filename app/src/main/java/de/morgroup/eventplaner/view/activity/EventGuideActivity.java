package de.morgroup.eventplaner.view.activity;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Toolbar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.fragment.guide.GuideEventLinkFragment;

public class EventGuideActivity extends AppCompatActivity {

    // init
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference ref;

    private AppBarConfiguration appBarConfiguration;
    private Event event;

    private boolean finish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // lädt standart activity layout file in dem die fragments aufgreufen werden
        setContentView(R.layout.activity_event_guide);

        ref = db.collection("events").document();
        event = new Event();
        // bekommen die id der referenz und setzen die id als atributt
        event.setId(ref.getId());
        // setzen ersteller als owner
        event.setOwner(firebaseUser.getUid());
        // setzen ersteller in memberliste
        event.setMember(new ArrayList<String>() {{
            add(firebaseUser.getUid());
        }});

        // navcontroller laden
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_event_guide);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

    }

    // standard methode bei zurückknopf drücken vom handy
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_event_guide);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    // methode für die fragments zum bekommen des grade im oncreate erstellten event
    public Event getEvent() {
        return event;
    }

    // methode um das erstellte event-model auf der firestore datenbank zu speichern
    public void saveEvent() {
        //ADD NEW EVENT
        ref.set(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseAuth.AuthStateListener firebaseAuthListener = auth -> {
            firebaseUser.reload();
        };
    }

    // set if event guid finished
    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    // wenn auf zurück gedrückt wird und das event schon fertig erstellt wurde (befinden wir uns im linkfragment) wird der komplette guide beendet
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (finish == true) {
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}