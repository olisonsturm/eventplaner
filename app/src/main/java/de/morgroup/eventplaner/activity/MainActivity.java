package de.morgroup.eventplaner.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.util.DownloadImageFromInternet;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

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

        footerPB = findViewById(R.id.footer_pb);
        footerName = findViewById(R.id.footer_name);
        footerEmail = findViewById(R.id.footer_mail);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        DownloadImageFromInternet downloadImageFromInternet;

        //set Footer Image
        if (user.getPhotoUrl() == null) {
            footerPB.setImageResource(R.drawable.ic_launcher);
        } else {
            downloadImageFromInternet = new DownloadImageFromInternet(footerPB);
            downloadImageFromInternet.execute(user.getPhotoUrl().toString());
        }
        //set Footer Name
        footerName.setText(user.getDisplayName());
        //set Footer Email
        footerEmail.setText(user.getEmail());

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

    // get Image from URL
    protected Bitmap urlImage(String... urls) {
        String imageURL = urls[0];
        Bitmap bimage = null;
        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            bimage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bimage;
    }

}