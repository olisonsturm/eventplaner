package de.morgroup.eventplaner.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import de.morgroup.eventplaner.view.fragment.AllEventsFragment;
import de.morgroup.eventplaner.view.fragment.OwnEventsFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private int tabs;

    public PagerAdapter(@NonNull @NotNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabs = tabs;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllEventsFragment();
            case 1:
                return new OwnEventsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
