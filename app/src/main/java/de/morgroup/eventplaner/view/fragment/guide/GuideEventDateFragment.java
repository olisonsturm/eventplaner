package de.morgroup.eventplaner.view.fragment.guide;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.Timestamp;

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
    DatePicker picker;

    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_date, container, false);
        ButterKnife.bind(this, view);
        event = ((EventGuideActivity) getActivity()).getEvent();
        //set min date
        picker.setMinDate(System.currentTimeMillis() - 1000);

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
        nextPage();
    }

    private void nextPage() {
        Date date = getDateFromDatePicker(picker);
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

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(year, month, day);

        return calendar.getTime();
    }

}