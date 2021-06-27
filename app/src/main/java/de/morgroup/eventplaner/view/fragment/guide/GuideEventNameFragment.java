package de.morgroup.eventplaner.view.fragment.guide;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.listener.OnSwipeTouchListener;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;


public class GuideEventNameFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userDB = db.collection("users")
            .document(firebaseUser.getUid());

    private StorageReference storage;
    private static final int IMAGE_REQUEST = 123;
    private StorageTask uploadTask;

    Event event;

    @BindView(R.id.RelativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.event_name_edit_text)
    TextView name;
    @BindView(R.id.event_thumbnail_image_view)
    ImageView thumbnail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_name, container, false);
        ButterKnife.bind(this, view);

        storage = FirebaseStorage.getInstance().getReference("event_uploads");

        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                getActivity().finish();
            }

            public void onSwipeLeft() {
                nextPage();
            }
        });

        return view;
    }

    // der pfeil um auf die nächste seite/fragment zu kommen
    @OnClick(R.id.open_event)
    void onNextPagePress() {
        nextPage();
    }

    // wenn auf das bild gedrückt wird kann man dem event ein eigenes thumbnail hinzufügen
    @OnClick(R.id.event_thumbnail_image_view)
    void onThumbnailPress() {
        openImage();
    }

    private void nextPage() {
        if (!TextUtils.isEmpty(name.getText().toString())) {

            // laden das erstellte event aus der activity
            event = ((EventGuideActivity) getActivity()).getEvent();

            // setzen des eventnamens
            event.setName(name.getText().toString());

            // nächste seite wird aufgerufen
            Navigation.findNavController(getView()).navigate(R.id.action_EventNameFragment_to_EventDateFragment);

        } else {
            // popupmessage falls nicht ausgefüllt
            Toast.makeText(getContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
        }
    }

    // beenden des guides
    @OnClick(R.id.close_guide)
    void onClosePress() {
        getActivity().finish();
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    /*
    *
    * TODO: delete event storage image, if event not complete created
    *
    * */
    private void uploadImage(Uri imageUri) {
        event = ((EventGuideActivity) getActivity()).getEvent();
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.uploading));
        pd.show();
        if (imageUri != null) {
            final StorageReference fileReference = storage.child(event.getId() + ".jpg");
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        // den generierten link des bildes wird jetzt dem event hinzugefügtr als string attribut
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        // setzen den link als attribut um in später per event model abzufragen
                        event.setThumbnailUrl(mUri);
                        // was ist glide erklärung
                        Glide.with(getContext())
                                .load(mUri)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                                .into(thumbnail);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

    // sobald ein bild ausgewählt wurde in der gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST) {
            // bild bekommen und mit der methode uploadimage auf unser firebase storage hochladen
            uploadImage(data.getData());
        }
    }
}