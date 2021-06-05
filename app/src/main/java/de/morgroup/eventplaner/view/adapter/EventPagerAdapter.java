package de.morgroup.eventplaner.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import de.morgroup.eventplaner.view.fragment.MainAllEventsFragment;
import de.morgroup.eventplaner.view.fragment.MainOwnEventsFragment;

public class EventPagerAdapter extends FragmentPagerAdapter {

    private int tabs;

    public EventPagerAdapter(@NonNull @NotNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabs = tabs;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainAllEventsFragment();
            case 1:
                return new MainOwnEventsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
