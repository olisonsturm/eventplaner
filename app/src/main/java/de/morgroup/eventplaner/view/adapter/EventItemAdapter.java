package de.morgroup.eventplaner.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.User;
import de.morgroup.eventplaner.view.activity.EventActivity;

public class EventItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListenerRegistration listenerRegistration;

    private Context context;
    private List<Object> itemList;
    private FirebaseUser firebaseUser;

    private boolean isOwner;
    private boolean isMenuOpen = false;

    class EventViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        TextView name, time, day, month, owner;
        ImageView thumbnail, menu;

        public EventViewHolder(View view) {
            super(view);
            card = (CardView) view.findViewById(R.id.card_view);
            name = (TextView) view.findViewById(R.id.event_item_name);
            time = (TextView) view.findViewById(R.id.event_item_time);
            day = (TextView) view.findViewById(R.id.event_item_day);
            month = (TextView) view.findViewById(R.id.event_item_month);
            owner = (TextView) view.findViewById(R.id.event_item_owner);
            thumbnail = (ImageView) view.findViewById(R.id.event_item_thumbnail);
            menu = (ImageView) view.findViewById(R.id.event_item_menu);
        }

        void bindData(int position) {
            // get event item
            Event item = (Event) itemList.get(position);
            // set information
            name.setText(item.getName().toUpperCase());
            time.setText(item.getTime().toUpperCase());

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = dateFormat.format(item.getDay().toDate());

            String d = date.split("-")[0];
            String m = getMonthForInt(Integer.parseInt(date.split("-")[1]));
            String y = date.split("-")[2];

            day.setText(d);
            month.setText(m);

            db.collection("users").document(item.getOwner()).addSnapshotListener((documentSnapshot, e) -> {
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
            Glide.with(context).load(item.getThumbnailUrl()).into(thumbnail);

            menu.setOnClickListener(view -> {
                if (!isMenuOpen) {
                    isMenuOpen = true;
                    if (item.getOwner().equals(firebaseUser.getUid())) {
                        showPopupMenuOwner(menu, item.getId(), item.getName());
                    } else {
                        showPopupMenuMember(menu, item.getId(), item.getName());
                    }
                }
            });

            card.setOnClickListener(view -> {
                Intent intent = new Intent(context, EventActivity.class);
                Gson gson = new Gson();
                String itemGson = gson.toJson(item);
                intent.putExtra("event", itemGson);
                context.startActivity(intent);
            });
        }

    }

    public EventItemAdapter(Context context, List<Object> itemList, FirebaseUser firebaseUser) {
        this.context = context;
        this.itemList = itemList;
        this.firebaseUser = firebaseUser;
    }

    @NotNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventViewHolder) {
            ((EventViewHolder) holder).bindData(position);
        }
    }

    // Showing popup menu when tapping on 3 dots
    private void showPopupMenuOwner(View view, String id, String name) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);

        SpannableString s = new SpannableString(context.getResources().getString(R.string.item_menu_event_löschen));
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        popup.getMenu().add(s);

        popup.setOnMenuItemClickListener(menu -> {
            switch (menu.getItemId()) {
                case 0:
                    AlertDialog alertDialog0 = new AlertDialog.Builder(context)
                            .setTitle(name)
                            .setMessage(context.getResources().getString(R.string.dialog_event_löschen))
                            .setPositiveButton(context.getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                                        // delete event
                                        db.collection("events").document(id).delete();
                                    }
                            )
                            .setNegativeButton(context.getResources().getString(R.string.dialogProfileNegative), null)
                            .create();
                    alertDialog0.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.alertdialog_rounded));
                    alertDialog0.show();
                    return true;
                default:
                    return false;
            }
        });
        popup.setOnDismissListener(menu -> isMenuOpen = false);
        popup.show();
    }

    private void showPopupMenuMember(View view, String id, String name) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        
        popup.getMenu().add(context.getResources().getString(R.string.item_menu_event_verlassen));

        popup.setOnMenuItemClickListener(menu -> {
            switch (menu.getItemId()) {
                case 0:
                    AlertDialog alertDialog0 = new AlertDialog.Builder(context)
                            .setTitle(name)
                            .setMessage(context.getResources().getString(R.string.dialog_event_verlassen))
                            .setPositiveButton(context.getResources().getString(R.string.dialogProfilePositive), (dialog, which) -> {
                                // leave event
                                DocumentReference docRef = db.collection("events").document(id);
                                // Remove the uid from member field
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("member", FieldValue.arrayRemove(firebaseUser.getUid()));
                                // update event document
                                docRef.update(updates);
                            })
                            .setNegativeButton(context.getResources().getString(R.string.dialogProfileNegative), null)
                            .create();
                    alertDialog0.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.alertdialog_rounded));
                    alertDialog0.show();
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
