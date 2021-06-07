package de.morgroup.eventplaner.view.fragment.guide;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.listener.OnSwipeTouchListener;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;


public class GuideEventTimeFragment extends Fragment {

    @BindView(R.id.RelativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.event_time_start)
    TextView timeStart;
    @BindView(R.id.event_time_end)
    TextView timeEnd;
    @BindView(R.id.event_time_end_date)
    TextView dateEnd;
    @BindView(R.id.event_time_end_date_switch)
    Switch dateSwitch;

    Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_time, container, false);
        ButterKnife.bind(this, view);

        event = ((EventGuideActivity) getActivity()).getEvent();

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
        if (!TextUtils.isEmpty(timeStart.getText()) && !TextUtils.isEmpty(timeEnd.getText())) {
            if (!dateSwitch.isChecked() || !TextUtils.isEmpty(dateEnd.getText())) {
                event = ((EventGuideActivity) getActivity()).getEvent();

                if (!TextUtils.isEmpty(dateEnd.getText())) {
                    event.setTime(timeStart.getText().toString() + " - " + timeEnd.getText().toString() + " (" + dateEnd.getText().toString().substring(0, 6) + dateEnd.getText().toString().substring(8) + ")");
                } else {
                    event.setTime(timeStart.getText().toString() + " - " + timeEnd.getText().toString());
                }

                ((EventGuideActivity) getActivity()).saveEvent();

                Navigation.findNavController(getView()).navigate(R.id.action_EventTimeFragment_to_EventLinkFragment);
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.event_time_start)
    void onStartTimeClick() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerEnd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int m) {
                String minute = String.format("%02d", m);
                String time = hour + ":" + minute;
                if (time.contains(":00")) {
                    time = time.replace(":00", "");
                    timeStart.setText(time + " Uhr");
                } else {
                    timeStart.setText(time + " Uhr");
                }
            }
        }, hour, minute, true);
        timePickerEnd.show();
    }

    @OnClick(R.id.event_time_end)
    void onEndTimeClick() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerEnd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int m) {
                String minute = String.format("%02d", m);
                String time = hour + ":" + minute;
                if (time.contains(":00")) {
                    time = time.replace(":00", "");
                    timeEnd.setText(time + " Uhr");
                } else {
                    timeEnd.setText(time + " Uhr");
                }
            }
        }, hour, minute, true);
        timePickerEnd.show();
    }

    @OnClick(R.id.event_time_end_time_switch)
    void onEndTimeSwitch() {
        if (timeEnd.isEnabled()) {
            timeEnd.setText("Open end");
            timeEnd.setEnabled(false);
        } else {
            timeEnd.setText(null);
            timeEnd.setEnabled(true);
        }
    }

    @OnClick(R.id.event_time_end_date_switch)
    void onEndDateSwitch() {
        if (dateEnd.isEnabled()) {
            dateEnd.setEnabled(false);
        } else {
            dateEnd.setEnabled(true);
        }
    }

    @OnClick(R.id.event_time_end_date)
    void onEndDateClick() {
        event = ((EventGuideActivity) getActivity()).getEvent();
        Calendar currentDate = Calendar.getInstance();
        int y = currentDate.get(Calendar.YEAR);
        int d = currentDate.get(Calendar.MONTH);
        int m = currentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker;
        datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date date = new GregorianCalendar(year, month - 1, dayOfMonth).getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                dateEnd.setText(dateFormat.format(date));
            }
        }, y, d, m);
        Date startDate = event.getDay().toDate() ;
        datePicker.getDatePicker().setMinDate(startDate.getTime() + (1000 * 60 * 60 * 24)); // plus one day
        datePicker.show();
    }

    @OnClick(R.id.close_guide)
    void onClosePress() {
        getActivity().finish();
    }

}