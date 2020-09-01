package com.example.work.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class FragmentPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> tabCount = new ArrayList<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<String> tabTitle = new ArrayList<>();

    public FragmentPageAdapter(FragmentManager fm, int behaviour) {
        super(fm,behaviour);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        return tabCount.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return "Tab" + (position + 1);
    }

    public void addFragment(Fragment fragment, String title) {
        tabCount.add(fragment);
        tabTitle.add(title);
    }


}
