package de.morgroup.eventplaner.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.util.DownloadImageFromInternet;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private DrawerLayout drawer;
    private ImageView headerPB;
    private TextView headerName, headerMail;
    private TextView accountFirstLastName, accountNick, accountEmail, accountMobile, accountAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        FirebaseUser user = firebaseAuth.getCurrentUser();

        headerPB = findViewById(R.id.header_pb);
        headerName = findViewById(R.id.header_name);
        headerMail= findViewById(R.id.header_mail);

        accountFirstLastName = findViewById(R.id.account_first_last_name);
        accountNick = findViewById(R.id.account_nick);
        accountEmail = findViewById(R.id.account_email);
        accountMobile = findViewById(R.id.account_mobile);
        accountAddress = findViewById(R.id.account_address);

        //set Profile Image
        DownloadImageFromInternet downloadImageFromInternet;
        if (user.getPhotoUrl() == null) {
            headerPB.setImageResource(R.drawable.ic_launcher);
        } else {
            downloadImageFromInternet = new DownloadImageFromInternet(headerPB);
            downloadImageFromInternet.execute(user.getPhotoUrl().toString());
        }

        //set Name
        headerName.setText(user.getDisplayName());
        accountFirstLastName.setText(user.getDisplayName());

        //set Email
        headerMail.setText(user.getEmail());
        accountEmail.setText(user.getEmail());


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