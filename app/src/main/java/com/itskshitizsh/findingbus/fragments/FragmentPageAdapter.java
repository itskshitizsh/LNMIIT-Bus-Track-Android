package com.itskshitizsh.findingbus.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentPageAdapter extends FragmentPagerAdapter {

    public FragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Bus1Fragment();
        }
        if (position == 1) {
            return new Bus2Fragment();
        }
        if (position == 2) {
            return new Bus3Fragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
