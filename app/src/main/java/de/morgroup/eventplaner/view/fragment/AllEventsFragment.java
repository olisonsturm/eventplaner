package de.morgroup.eventplaner.view.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.model.EventItem;
import de.morgroup.eventplaner.view.adapter.EventItemAdapter;

public class AllEventsFragment extends Fragment {


    private RecyclerView recyclerView;
    private EventItemAdapter eventItemAdapter;
    private List eventItemList;

    public AllEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        eventItemList = new ArrayList<>();
        eventItemAdapter = new EventItemAdapter(getContext(), eventItemList);

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(eventItemAdapter);

        initEventItems();

        return view;
    }

    //  few events for testing
    private void initEventItems() {
        int[] paintings = new int[]{
                R.drawable.img_banner,
                R.drawable.img_placeholder,
                R.drawable.img_banner,
                R.drawable.img_placeholder,
                R.drawable.img_banner,
                R.drawable.img_placeholder,
                R.drawable.img_banner,
                R.drawable.img_placeholder,
                R.drawable.img_banner,
                R.drawable.img_placeholder};

        EventItem item = new EventItem("Silvester", "Olison", paintings[0]);
        eventItemList.add(item);

        item = new EventItem("Weihnachten", "Olison", paintings[1]);
        eventItemList.add(item);

        item = new EventItem("Geburtstag", "Marvin", paintings[2]);
        eventItemList.add(item);

        item = new EventItem("Ostern", "Marvin", paintings[3]);
        eventItemList.add(item);

        item = new EventItem("Geburtstag", "Robin", paintings[4]);
        eventItemList.add(item);

        item = new EventItem("Urlaub", "Robin", paintings[5]);
        eventItemList.add(item);

        item = new EventItem("Sommerparty", "Olison", paintings[6]);
        eventItemList.add(item);

        item = new EventItem("Geburtstag", "Olison", paintings[7]);
        eventItemList.add(item);

        item = new EventItem("Familienfete", "Marvin", paintings[8]);
        eventItemList.add(item);

        item = new EventItem("Poolparty", "Marvin", paintings[9]);
        eventItemList.add(item);

        eventItemAdapter.notifyDataSetChanged();
    }

    // give equal margin around grid item
    public class EventItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable divider;

        public EventItemDecoration(Context context) {
            divider = context.getResources().getDrawable(R.drawable.recycler_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

    // Converting dp to pixel
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}