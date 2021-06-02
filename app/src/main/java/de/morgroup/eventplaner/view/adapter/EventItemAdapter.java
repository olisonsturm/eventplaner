package de.morgroup.eventplaner.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.EventItem;

public class EventItemAdapter extends RecyclerView.Adapter {

    private Context context;
    private List itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);

        }

    }

    public EventItemAdapter(Context mContext, List itemList) {
        this.context = mContext;
        this.itemList = itemList;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_event_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        EventItem item = (EventItem) itemList.get(position);
        //((MyViewHolder) holder).title.setText(item.getName());
        //((MyViewHolder) holder).count.setText("Erstellt von: " + item.getOwner());

        // loading album cover using Glide library
        Glide.with(context).load(item.getThumbnailUrl()).into(((MyViewHolder) holder).thumbnail);

        ((MyViewHolder) holder).overflow.setOnClickListener(view -> showPopupMenu(((MyViewHolder) holder).overflow));
    }


    // Showing popup menu when tapping on 3 dots
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_item, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();

    }

    // Click listener for popup menu items
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_like:
                    Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_unlike:
                    Toast.makeText(context, "Unlike", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
