package com.aurora.phone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.contact.interfaces.FavouritesLiveData;
import com.aurora.phone.GridSpacingDecoration;
import com.aurora.phone.R;
import com.aurora.phone.adapter.FavouriteSection;
import com.aurora.phone.sheet.FavouriteMenu;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class FavouriteFragment extends Fragment implements FavouriteSection.ClickListener {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FavouritesLiveData favouritesLiveData = new FavouritesLiveData(requireContext());
        favouritesLiveData.observe(this, cursor -> {
            List<Contact> contactList = Contact.getLiteContactList(requireContext(), cursor);
            setupRecycler(contactList);
        });
    }

    private void setupRecycler(List<Contact> contactList) {
        SectionedRecyclerViewAdapter viewAdapter = new SectionedRecyclerViewAdapter();
        viewAdapter.addSection(new FavouriteSection(requireContext(), contactList, this));

        final int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_medium);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.addItemDecoration(new GridSpacingDecoration(2, spacingInPixels, true, 0));
        recyclerView.setAdapter(viewAdapter);
    }


    @Override
    public void onClickItem(@NonNull Contact favourite, int position) {
        FavouriteMenu favouriteMenu = FavouriteMenu.getInstance();
        favouriteMenu.setPosition(position);
        favouriteMenu.setContact(favourite);
        favouriteMenu.show(getChildFragmentManager(), "FAVOURITE_MENU");
    }

    @Override
    public void onLongClickItem(@NonNull Contact favourite, int position) {

    }
}
