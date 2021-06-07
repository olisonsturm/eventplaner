package de.morgroup.eventplaner.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.User;
import de.morgroup.eventplaner.view.adapter.EventPagerAdapter;

public class EventActivity extends AppCompatActivity {

    // init
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDB;

    private ListenerRegistration listenerRegistration;

    Event event;

    public Event getEvent() {
        return event;
    }

    User owner;

    public User getOwner() {
        return owner;
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_event)
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
    TextView ownerName;

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
        owner = new User();
        userDB = db.collection("users")
                .document(event.getOwner());

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

        adapter = new EventPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount());
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
        listenerRegistration = userDB.addSnapshotListener((documentSnapshot, e) -> {
            // preventing errors
            if (e != null) {
                return;
            }
            // getting data and update
            if (documentSnapshot.exists()) {
                // receive the user object from db
                owner = documentSnapshot.toObject(User.class);
                ownerName.setText(owner.getFirstname() + " " + owner.getLastname());
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

    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (event.getOwner().equals(firebaseUser.getUid())) {
            //owner
            inflater.inflate(R.menu.event_menu_owner, menu);
        } else {
            //member
            inflater.inflate(R.menu.event_menu_member, menu);
        }
        Method menuMethod = null;
        try {
            menuMethod = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            menuMethod.setAccessible(true);
            menuMethod.invoke(menu, true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (event.getOwner().equals(firebaseUser.getUid())) {
            //owner
            switch (item.getItemId()) {
                case R.id.event_delete:
                    AlertDialog alertDialog0 = new AlertDialog.Builder(this)
                            .setTitle(event.getName())
                            .setMessage(getResources().getString(R.string.dialog_event_löschen))
                            .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                                        // delete event
                                        db.collection("events").document(event.getId()).delete();
                                        finish();
                                    }
                            )
                            .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                            .create();
                    alertDialog0.getWindow().setBackgroundDrawableResource(R.drawable.alertdialog_rounded);
                    alertDialog0.show();
                    break;
                case R.id.event_mute:
                    Toast.makeText(getApplicationContext(), "not included", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.event_edit:
                    Toast.makeText(getApplicationContext(), "not included", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            //member
            switch (item.getItemId()) {
                case R.id.event_leave:
                    AlertDialog alertDialog0 = new AlertDialog.Builder(getApplicationContext())
                            .setTitle(event.getName())
                            .setMessage(getResources().getString(R.string.dialog_event_verlassen))
                            .setPositiveButton(getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                                // leave event
                                DocumentReference docRef = db.collection("events").document(event.getId());
                                // Remove the uid from member field
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("member", FieldValue.arrayRemove(firebaseUser.getUid()));
                                // update event document
                                docRef.update(updates);
                                finish();
                            })
                            .setNegativeButton(getResources().getString(R.string.dialogProfileNegative), null)
                            .create();
                    alertDialog0.getWindow().setBackgroundDrawableResource(R.drawable.alertdialog_rounded);
                    alertDialog0.show();
                    break;
                case R.id.event_mute:
                    Toast.makeText(getApplicationContext(), "not included", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}