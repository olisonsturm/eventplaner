package de.morgroup.eventplaner.view.fragment.guide;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventActivity;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;


public class GuideEventLinkFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Event event;

    Uri dynamicLinkUri;

    @BindView(R.id.link)
    TextView link;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_link, container, false);
        ButterKnife.bind(this, view);

        event = ((EventGuideActivity) getActivity()).getEvent();

        ((EventGuideActivity) getActivity()).setFinish(true);

        createShortJoinLink();

        return view;
    }

    @OnClick(R.id.link)
    void onLinkPress() {
        shareLink(dynamicLinkUri);
    }

    @OnClick(R.id.open_event)
    void onFinishPress() {
        event = ((EventGuideActivity) getActivity()).getEvent();
        event.setJoinLink(dynamicLinkUri.toString());
        ((EventGuideActivity) getActivity()).saveEvent();

        Intent intent = new Intent(getContext(), EventActivity.class);
        Gson gson = new Gson();
        String itemGson = gson.toJson(event);
        intent.putExtra("event", itemGson);
        startActivity(intent);
        getActivity().finish();
    }

    public void createShortJoinLink() {

        Uri linkWithId = Uri.parse("https://www.eventplaner.eu/join")
                .buildUpon()
                .appendQueryParameter("eventId", event.getId())
                .build();

        Uri imageUrl;
        if (!TextUtils.isEmpty(event.getThumbnailUrl())) {
            imageUrl = Uri.parse(event.getThumbnailUrl());
        } else {
            imageUrl = Uri.parse("https://firebasestorage.googleapis.com/v0/b/eventplaner-b44ff.appspot.com/o/app_image.png?alt=media&token=0888903d-2030-4cb6-82b5-f3af0aa67d06");
        }

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(linkWithId)
                .setDomainUriPrefix("https://eventplaner.eu/join")
                // Set parameters
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(event.getName().toUpperCase())
                                .setDescription("Erstellt mit EventPlaner")
                                .setImageUrl(imageUrl)
                                .build())
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder()
                                .setFallbackUrl(Uri.parse("http://beta.eventplaner.eu"))
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener((EventGuideActivity) getContext(), new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            link.setText(shortLink.toString());
                            dynamicLinkUri = shortLink;
                        }
                    }
                });
    }

    public void shareLink(Uri myDynamicLink) {
        Intent sendIntent = new Intent();
        String msg = "Joine meinem Event: " + myDynamicLink;
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, ""));
    }


}