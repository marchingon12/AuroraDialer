package com.aurora.phone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aurora.phone.R;
import com.aurora.phone.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainFragment extends Fragment {

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    private Context context;
    private TabAdapter tabAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public void switchToContacts() {
        pager.setCurrentItem(2, true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tabAdapter = new TabAdapter(getChildFragmentManager());
        tabAdapter.addFragment(FavouriteFragment.newInstance(), getString(R.string.title_favourite));
        tabAdapter.addFragment(CallLogFragment.newInstance(), getString(R.string.title_recent));
        tabAdapter.addFragment(ContactsFragment.newInstance(), getString(R.string.title_contact));
        pager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(pager);
    }

    @OnClick(R.id.search_bar)
    public void openSearchFragment() {
        getChildFragmentManager().beginTransaction()
                .add(R.id.coordinator, SearchFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }
}
