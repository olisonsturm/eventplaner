package de.morgroup.eventplaner.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.model.Voting;
import de.morgroup.eventplaner.view.activity.EventActivity;
import de.morgroup.eventplaner.view.activity.VotingActivity;

public class EventVotingItemAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Object> itemList;
    private Event event;

    class VotingViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        TextView name;

        public VotingViewHolder(View view) {
            super(view);
            card = (CardView) view.findViewById(R.id.card_view);
            name = (TextView) view.findViewById(R.id.name);
        }

        void bindData(int position) {
            // get event item
            Voting voting = (Voting) itemList.get(position);
            // set information
            name.setText(voting.getName());

            card.setOnClickListener(view -> {
                Intent intent = new Intent(context, VotingActivity.class);

                Gson gson1 = new Gson();
                String itemGson1 = gson1.toJson(voting);
                intent.putExtra("voting", itemGson1);

                Gson gson2 = new Gson();
                String itemGson2 = gson2.toJson(event);
                intent.putExtra("event", itemGson2);

                context.startActivity(intent);
            });

        }

    }

    public EventVotingItemAdapter(Context context, List<Object> itemList, Event event) {
        this.context = context;
        this.itemList = itemList;
        this.event = event;
    }

    @NotNull
    @Override
    public EventVotingItemAdapter.VotingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventVotingItemAdapter.VotingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.voting_list_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventVotingItemAdapter.VotingViewHolder) {
            ((EventVotingItemAdapter.VotingViewHolder) holder).bindData(position);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
