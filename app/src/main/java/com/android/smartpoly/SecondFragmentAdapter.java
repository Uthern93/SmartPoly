package com.android.smartpoly;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SecondFragmentAdapter extends FragmentStateAdapter {
    public SecondFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new TrackFragment();
        }
        return new CourseFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
