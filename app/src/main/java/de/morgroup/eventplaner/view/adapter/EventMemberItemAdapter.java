package de.morgroup.eventplaner.view.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.User;

public class EventMemberItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> itemList;
    private Event event;
    private FirebaseUser firebaseUser;

    class MemberViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView name, rank;
        ImageView photo;

        public MemberViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.member_item_layout);
            name = (TextView) view.findViewById(R.id.member_item_name);
            rank = (TextView) view.findViewById(R.id.member_item_rank);
            photo = (ImageView) view.findViewById(R.id.member_item_photo);
        }

        void bindData(int position) {
            // get event item
            User user = (User) itemList.get(position);
            // set information
            name.setText(user.getFirstname() + " " + user.getLastname());
            rank.setText(context.getResources().getString(R.string.member));


            // loading event thumbnail url by using Glide library
            if (user.getPhotourl() != null) {
                Glide.with(context)
                        .load(user.getPhotourl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                        .into(photo);
            } else {
                Glide.with(context)
                        .load(R.drawable.img_placeholder)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                        .into(photo);
            }

            layout.setOnClickListener(view -> {
                showPopupMenuDialog(user);
            });

        }

    }

    // dialog menu
    private void showPopupMenuDialog(User user) {
        if (event.getOwner().equals(firebaseUser.getUid())) {
            final String[] option = {"Whatsapp-Chat öffnen", user.getNickname() + " entfernen"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.alertdialog_item, option);
            AlertDialog.Builder dialogMenu = new AlertDialog.Builder(context);
            dialogMenu.setAdapter(adapter, (dialog, which) -> {
                switch (which) {
                    case 0:
                        // nachricht schreiben per Whatsapp
                        Uri uri = Uri.parse("smsto:" + user.getMobile());
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        intent.setPackage("com.whatsapp");
                        context.startActivity(Intent.createChooser(intent, ""));
                        return;
                    case 1:
                        Map<String, Object> update = new HashMap<>();
                        update.put("member", FieldValue.arrayRemove(user.getUid()));
                        FirebaseFirestore.getInstance().collection("events")
                                .document(event.getId()).update(update);
                        return;
                    default:
                        return;
                }
            });
            final AlertDialog alertDialog = dialogMenu.create();
            alertDialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog.show();
        } else {
            final String[] option = {"Whatsapp-Chat öffnen"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.alertdialog_item, option);
            AlertDialog.Builder dialogMenu2 = new AlertDialog.Builder(context);
            dialogMenu2.setAdapter(adapter, (dialog, which) -> {
                switch (which) {
                case 0:
                    // nachricht schreiben per Whatsapp
                    Uri uri = Uri.parse("smsto:" + user.getMobile());
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.setPackage("com.whatsapp");
                    context.startActivity(Intent.createChooser(intent, ""));
                    return;
                default:
                    return;
            }
            });
            final AlertDialog alertDialog2 = dialogMenu2.create();
            alertDialog2.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.alertdialog_rounded));
            alertDialog2.show();
        }
    }

    public EventMemberItemAdapter(Context context, List<Object> itemList, Event event, FirebaseUser firebaseUser) {
        this.context = context;
        this.itemList = itemList;
        this.event = event;
        this.firebaseUser = firebaseUser;
    }

    @NotNull
    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item, parent, false));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MemberViewHolder) {
            ((MemberViewHolder) holder).bindData(position);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
