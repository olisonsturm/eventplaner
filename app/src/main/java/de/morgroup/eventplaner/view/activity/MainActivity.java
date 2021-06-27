package de.morgroup.eventplaner.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.User;
import de.morgroup.eventplaner.view.adapter.MainPagerAdapter;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // init
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDB = db.collection("users")
            .document(firebaseUser.getUid());

    private ListenerRegistration listenerRegistration;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager pager;
    @BindView(R.id.footer_pb)
    ImageView footerPB;
    @BindView(R.id.footer_name)
    TextView footerName;
    @BindView(R.id.footer_mail)
    TextView footerMail;

    ActionBarDrawerToggle toggle;
    androidx.viewpager.widget.PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_all_events);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState(); // open or close

        adapter = new MainPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount());
        pager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
                navigationView.getMenu().getItem(tab.getPosition()).setChecked(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    @OnClick(R.id.fab_create_event)
    void onFabCreateEventPress() {
        startActivity(new Intent(getApplicationContext(), EventGuideActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deeplink = null;
                    if (pendingDynamicLinkData != null){
                        deeplink = pendingDynamicLinkData.getLink();
                        String eventId = deeplink.getQueryParameter("eventId");
                        Map<String, Object> update = new HashMap<>();
                        update.put("member", FieldValue.arrayUnion(firebaseUser.getUid()));
                        DocumentReference eventRef = db.collection("events").document(eventId);
                        eventRef.update(update);
                        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String name = (String) task.getResult().get("name");
                                    Toast.makeText(getApplicationContext(), "Du bist dem Event " + name.toUpperCase() + " beigetreten", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }


                });

        // getting data by listener
        listenerRegistration = userDB.addSnapshotListener((documentSnapshot, e) -> {
            // preventing errors
            if (e != null) {
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
                if (user.getPhotourl() != null) {
                    Glide.with(getApplicationContext())
                            .load(user.getPhotourl())
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(footerPB);
                } else {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.img_placeholder)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(footerPB);
                }
                footerName.setText(firstname + " " + lastname);
                footerMail.setText(email);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_all_events:
                pager.setCurrentItem(0, true);
                break;
            case R.id.nav_own_events:
                pager.setCurrentItem(1, true);
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.nav_logout:
                // logout-code
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.account_logout))
                        .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                                    FirebaseAuth.getInstance().signOut(); //logout
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //startet LoginActivity
                                    finish(); //beendet aktuelle Acitivity
                                }
                        )
                        .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                        .create();
                alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alertdialog_rounded));
                alertDialog.show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}