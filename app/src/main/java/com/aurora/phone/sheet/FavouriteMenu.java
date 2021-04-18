package com.aurora.phone.sheet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.R;
import com.aurora.phone.manager.CallManager;
import com.aurora.phone.manager.FavouritesManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteMenu extends BottomSheetDialogFragment {

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private Context context;
    private int position;
    private Contact contact;

    public static FavouriteMenu getInstance() {
        return new FavouriteMenu();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_bottom, container, false);
        ButterKnife.bind(this, view);
        navigationView.inflateMenu(R.menu.menu_favourites);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_remove:
                    FavouritesManager.removeFromFavourites(context, contact);

                    break;
                case R.id.action_call:
                    CallManager.call(context, contact.getPhoneList().get(0).getMainData(), 2);
                    break;
            }
            dismissAllowingStateLoss();
            return false;
        });
    }


}
