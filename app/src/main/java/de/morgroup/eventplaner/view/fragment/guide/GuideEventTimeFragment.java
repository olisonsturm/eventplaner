package de.morgroup.eventplaner.view.fragment.guide;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.listener.OnSwipeTouchListener;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;


public class GuideEventTimeFragment extends Fragment {

    @BindView(R.id.RelativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.event_time_text_view)
    TextView time;

    Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_time, container, false);
        ButterKnife.bind(this, view);

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
        if (!TextUtils.isEmpty(time.getText())) {

            event = ((EventGuideActivity) getActivity()).getEvent();

            event.setTime(time.getText().toString());

            ((EventGuideActivity) getActivity()).saveEvent();

            Navigation.findNavController(getView()).navigate(R.id.action_EventTimeFragment_to_EventLinkFragment);

        } else {
            Toast.makeText(getContext(),getResources().getString(R.string.fill_all_fields),Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.close_guide)
    void onClosePress() {
        getActivity().finish();
    }

}