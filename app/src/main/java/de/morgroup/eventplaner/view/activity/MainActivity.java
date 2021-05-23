package de.morgroup.eventplaner.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.view.fragment.EventsFragment;
import de.morgroup.eventplaner.db.User;
import de.morgroup.eventplaner.util.ProfileImageFB;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // init
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final DocumentReference userDB = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private ListenerRegistration listenerRegistration;

    private DrawerLayout drawer;
    private ImageView footerPB;
    private TextView footerName, footerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new EventsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_events);
        }

        // dekl. widgets
        footerPB = findViewById(R.id.footer_pb);
        footerName = findViewById(R.id.footer_name);
        footerEmail = findViewById(R.id.footer_mail);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // catch any update from firestore db

        // getting data by listener
        listenerRegistration = userDB.addSnapshotListener((documentSnapshot, e) -> {
            // preventing errors
            if (e != null) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            // getting data and update
            if (documentSnapshot.exists()) {

                // receive the user object from db
                User user = documentSnapshot.toObject(User.class);

                // cache the data
                String firstname = user.getFirstname();
                String lastname = user.getLastname();
                String email = user.getEmail();

                // nav footer
                footerName.setText(firstname + " " + lastname);
                footerEmail.setText(email);

//                ProfileImageFromFirebase profileImageFromFirebase = new ProfileImageFromFirebase(footerPB);
//                profileImageFromFirebase.execute(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                new ProfileImageFB(footerPB).execute(user.getPhotourl());


            } else {
                footerName.setText("");
                footerEmail.setText("");
            }

        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_events:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new EventsFragment()).commit();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.nav_settings:
                //startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.nav_logout:
                // logout-code
                FirebaseAuth.getInstance().signOut(); //logout
                startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //startet LoginActivity
                finish(); //benendet aktuelle Acitivity
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}