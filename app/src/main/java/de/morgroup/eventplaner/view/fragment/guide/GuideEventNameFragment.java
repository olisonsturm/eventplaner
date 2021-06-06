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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.morgroup.eventplaner.R;
import de.morgroup.eventplaner.listener.OnSwipeTouchListener;
import de.morgroup.eventplaner.model.Event;
import de.morgroup.eventplaner.view.activity.EventGuideActivity;


public class GuideEventNameFragment extends Fragment {

    Event event;

    @BindView(R.id.RelativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.event_name_edit_text)
    TextView name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_event_name, container, false);
        ButterKnife.bind(this, view);

        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                getActivity().finish();
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
        if (!TextUtils.isEmpty(name.getText())) {

            event = ((EventGuideActivity) getActivity()).getEvent();

            event.setName(name.getText().toString());
            event.setThumbnailUrl("https://www.bkgymswim.com.au/wp-content/uploads/2017/08/image_large.png");

            Navigation.findNavController(getView()).navigate(R.id.action_EventNameFragment_to_EventDateFragment);

        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.close_guide)
    void onClosePress() {
        getActivity().finish();
    }

}