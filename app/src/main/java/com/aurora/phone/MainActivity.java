package com.aurora.phone;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.ColorUtils;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.contact.interfaces.SearchLiveData;
import com.aurora.phone.adapter.T9Section;
import com.aurora.phone.util.ImageUtil;
import com.aurora.phone.util.Util;
import com.aurora.phone.util.ViewUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.layout_numpad)
    FrameLayout layoutNumpad;
    @BindView(R.id.fab_keypad)
    ExtendedFloatingActionButton keypadFab;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.txt_input_number)
    TextInputEditText textInputNumber;

    private BottomSheetBehavior bottomSheetBehavior;
    private SectionedRecyclerViewAdapter viewAdapter;
    private T9Section section;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    static boolean matchDestination(@NonNull NavDestination destination, @IdRes int destId) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != destId && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == destId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Util.checkPermissions(this);
        Util.checkDefaultDialer(this);

        setupNavigation();

        setupKeypad();
        test();
    }

    private void setupNavigation() {
        int backGroundColor = ViewUtil.getStyledAttribute(this, android.R.attr.colorBackground);
        bottomNavigationView.setBackgroundColor(ColorUtils.setAlphaComponent(backGroundColor, 245));

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //Avoid Adding same fragment to NavController, if clicked on current BottomNavigation item
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == bottomNavigationView.getSelectedItemId())
                return false;
            NavigationUI.onNavDestinationSelected(item, navController);
            return true;
        });

        //Check correct BottomNavigation item, if navigation_main is done programmatically
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            final Menu menu = bottomNavigationView.getMenu();
            final int size = menu.size();
            for (int i = 0; i < size; i++) {
                MenuItem item = menu.getItem(i);
                if (matchDestination(destination, item.getItemId())) {
                    item.setChecked(true);
                }
            }
        });
    }

    private void setupKeypad() {
        textInputNumber.setShowSoftInputOnFocus(false);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutNumpad);
        bottomSheetBehavior.setPeekHeight(0, true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        keypadFab.hide();
                        get();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        keypadFab.show();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float alpha = (255 * slideOffset);
                int backGroundColor = ViewUtil.getStyledAttribute(MainActivity.this, android.R.attr.colorBackground);
                recyclerView.setBackground(ImageUtil.getDynamicGradientDrawable(ColorUtils.setAlphaComponent(backGroundColor, (int) alpha), backGroundColor));
            }
        });

        keypadFab.setOnClickListener(v -> {
            keypadFab.hide();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        textInputNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void get() {
        if (section != null)
            return;

        SearchLiveData searchLiveData = new SearchLiveData(this);
        searchLiveData.observe(this, cursor -> {
            buildContacts(cursor);
        });
    }

    private void buildContacts(Cursor cursor) {
        Disposable disposable = Observable.fromCallable(() -> Contact
                .getSearchContactList(this, cursor))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    setupRecycler(contacts);
                }, err -> {

                });
        compositeDisposable.add(disposable);
    }

    private void setupRecycler(List<Contact> contactList) {
        viewAdapter = new SectionedRecyclerViewAdapter();
        section = new T9Section(this, contactList, (contact, position) -> {
            textInputNumber.setText(contact.getPhoneNumber());
        });
        viewAdapter.addSection(section);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(viewAdapter);
    }

    private void filterContacts(String query) {
        if (section == null)
            return;

        for (Section section : viewAdapter.getCopyOfSectionsMap().values()) {
            if (section instanceof T9Section) {
                ((FilterableSection) section).filter(query);
            }
        }
        viewAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.search_bar)
    public void openSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(intent, activityOptions.toBundle());
    }

    private void test() {
        //String a = CallLog.Calls.getLastOutgoingCall(this);
    }
}
