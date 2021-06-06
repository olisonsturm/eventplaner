package de.morgroup.eventplaner.view.fragment.guide;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.listener.OnSwipeTouchListener;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;


public class GuideEventDateFragment extends Fragment {

    @BindView(R.id.RelativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.event_calendar_view)
    CalendarView calendarView;

    private Event event;
    private Calendar dateSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_date, container, false);
        ButterKnife.bind(this, view);
        event = ((EventGuideActivity) getActivity()).getEvent();
        //set min date
        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        // get a calendar instance
        dateSelected = Calendar.getInstance();
        // calendar view date change listener
        calendarView.setOnDateChangeListener((v, year, month, day) -> {
            // set the calendar date as calendar view selected date
            dateSelected.set(year, month, day);
            dateSelected.set(Calendar.SECOND, 0);
            dateSelected.set(Calendar.MINUTE, 0);
            dateSelected.set(Calendar.HOUR_OF_DAY, 0);
        });

        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                Navigation.findNavController(getView()).navigateUp();
            }
            public void onSwipeLeft() {
                nextPage();
            }
        });

        return view;
    }

    @OnClick(R.id.open_event)
    void onNextPagePress() {

    }

    private void nextPage() {
        Date date = new Date(dateSelected.getTimeInMillis());
        Date today = new Date();
        today.setHours(0);

        event = ((EventGuideActivity) getActivity()).getEvent();

        event.setDay(new Timestamp(date));

        Navigation.findNavController(getView()).navigate(R.id.action_EventDateFragment_to_EventTimeFragment);
    }

    @OnClick(R.id.close_guide)
    void onClosePress() {
        getActivity().finish();
    }

}