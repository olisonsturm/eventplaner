package de.morgroup.eventplaner.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.User;
import de.morgroup.eventplaner.util.ProfileImageFB;
import de.morgroup.eventplaner.view.activity.LoginActivity;
import de.morgroup.eventplaner.view.fragment.OwnEventsFragment;

public class EventItemAdapter extends RecyclerView.Adapter {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListenerRegistration listenerRegistration;

    private Context context;
    private List<Object> itemList;
    private boolean isOwner;

    private boolean isMenuOpen = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, time, day, month, owner;
        ImageView thumbnail, menu;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.event_item_name);
            time = (TextView) view.findViewById(R.id.event_item_time);
            day = (TextView) view.findViewById(R.id.event_item_day);
            month = (TextView) view.findViewById(R.id.event_item_month);
            owner = (TextView) view.findViewById(R.id.event_item_owner);
            thumbnail = (ImageView) view.findViewById(R.id.event_item_thumbnail);
            menu = (ImageView) view.findViewById(R.id.event_item_menu);
        }

    }

    public EventItemAdapter(Context context, List<Object> itemList, boolean owner) {
        this.context = context;
        this.itemList = itemList;
        this.isOwner = owner;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        Event item = (Event) itemList.get(position);

        ((MyViewHolder) holder).name.setText(item.getName().toUpperCase());
        ((MyViewHolder) holder).time.setText(item.getTime().toUpperCase());
        ((MyViewHolder) holder).day.setText(item.getDay().toUpperCase());
        ((MyViewHolder) holder).month.setText(item.getMonth().toUpperCase());
        db.collection("users").document(item.getOwner()).addSnapshotListener((documentSnapshot, e) -> {
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
                // show the data
                ((MyViewHolder) holder).owner.setText(firstname + " " + lastname);
            }
        });

        // loading event thumbnail url by using Glide library
        Glide.with(context).load(item.getThumbnailUrl()).into(((MyViewHolder) holder).thumbnail);

        ((MyViewHolder) holder).menu.setOnClickListener(view -> {
            if (!isMenuOpen) {
                isMenuOpen = true;
                showPopupMenu(((MyViewHolder) holder).menu, item.getName(), isOwner);
            }
        });
    }


    // Showing popup menu when tapping on 3 dots
    private void showPopupMenu(View view, String name, boolean owner) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        if (!owner) {
            SpannableString s = new SpannableString(context.getResources().getString(R.string.item_menu_event_verlassen));
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            popup.getMenu().add(0, 0, 0, s);
        } else {
            SpannableString s = new SpannableString(context.getResources().getString(R.string.item_menu_event_löschen));
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            popup.getMenu().add(0, 1, 0, s);
        }
        popup.setOnMenuItemClickListener(menu -> {
            switch (menu.getItemId()) {
                case 0:
                    AlertDialog alertDialog0 = new AlertDialog.Builder(context)
                            .setTitle(name)
                            .setMessage(context.getResources().getString(R.string.dialog_event_verlassen))
                            .setPositiveButton(context.getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                                        // TODO: event verlassen
                                    }
                            )
                            .setNegativeButton(context.getResources().getString(R.string.dialogProfileNegative), null)
                            .create();
                    alertDialog0.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.alertdialog_rounded));
                    alertDialog0.show();
                    return true;
                case 1:
                    AlertDialog alertDialog1 = new AlertDialog.Builder(context)
                            .setTitle(name)
                            .setMessage(context.getResources().getString(R.string.dialog_event_löschen))
                            .setPositiveButton(context.getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                                        // TODO: event löschen
                                    }
                            )
                            .setNegativeButton(context.getResources().getString(R.string.dialogProfileNegative), null)
                            .create();
                    alertDialog1.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.alertdialog_rounded));
                    alertDialog1.show();
                    return true;
                default:
                    return false;
            }
        });
        popup.setOnDismissListener(menu -> isMenuOpen = false);
        popup.show();

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
