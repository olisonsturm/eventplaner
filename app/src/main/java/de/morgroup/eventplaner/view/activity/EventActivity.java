package de.morgroup.eventplaner.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.User;

public class EventActivity extends AppCompatActivity {

    // init
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDB = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private ListenerRegistration listenerRegistration;

    Event event;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager pager;
    @BindView(R.id.event_item_name)
    TextView name;
    @BindView(R.id.event_item_time)
    TextView time;
    @BindView(R.id.event_item_day)
    TextView day;
    @BindView(R.id.event_item_month)
    TextView month;
    @BindView(R.id.event_item_owner)
    TextView owner;
//    @BindView(R.id.event_item_thumbnail)
//    ImageView thumbnail;

    androidx.viewpager.widget.PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        event = getIncomingIntent();

        // set information
        name.setText(event.getName().toUpperCase());
        time.setText(event.getTime().toUpperCase());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(event.getDay().toDate());

        String d = date.split("-")[0];
        String m = getMonthForInt(Integer.parseInt(date.split("-")[1]));
        String y = date.split("-")[2];

        day.setText(d);
        month.setText(m);

        db.collection("users").document(event.getOwner()).addSnapshotListener((documentSnapshot, e) -> {
            // preventing errors
            if (e != null) {
                return;
            }
            // getting data and update
            if (documentSnapshot.exists()) {
                // receive the user object from db
                User user = documentSnapshot.toObject(User.class);
                // show the data
                owner.setText(user.getFirstname() + " " + user.getLastname());
            }
        });

        // loading event thumbnail url by using Glide library
//        Glide.with(getApplicationContext()).load(event.getThumbnailUrl()).into(thumbnail);

        //adapter = new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount());
        pager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
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

    @Override
    protected void onStart() {
        super.onStart();

        // catch any update from firestore db

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


            } else {

            }

        });
    }

    private Event getIncomingIntent() {
        if (getIntent().hasExtra("event")) {
            return (Event) new Gson().fromJson(getIntent().getStringExtra("event"), Event.class);
        }
        return null;
    }

    private String getMonthForInt(int num) {
        String month = "error";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 1 && num <= 12) {
            month = months[num - 1].substring(0, 3).toUpperCase() + ".";
        }
        return month;
    }

}