package com.aurora.phone.sheet;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.contact.interfaces.SearchLiveData;
import com.aurora.phone.R;
import com.aurora.phone.adapter.SearchSection;
import com.aurora.phone.adapter.T9Section;
import com.aurora.phone.view.KeypadView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class KeypadSheet extends BottomSheetDialogFragment {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.keypad_view)
    KeypadView keypadView;
    @BindView(R.id.layout_action_top)
    FrameLayout layoutActionTop;
    @BindView(R.id.txt_input_number)
    TextInputEditText textInputNumber;

    private Context context;


    private BottomSheetBehavior bottomSheetBehavior;
    private SectionedRecyclerViewAdapter viewAdapter;
    private T9Section section;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keypad, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textInputNumber.setShowSoftInputOnFocus(false);
        textInputNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (viewAdapter != null && section != null)
                    filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void get() {
        SearchLiveData searchLiveData = new SearchLiveData(context);
        searchLiveData.observe(this, cursor -> {
            buildContacts(cursor);
        });
    }

    private void buildContacts(Cursor cursor) {
        Disposable disposable = Observable.fromCallable(() -> Contact
                .getSearchContactList(context, cursor))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    setupRecycler(contacts);
                }, err -> {

                });
    }

    private void setupRecycler(List<Contact> contactList) {
        viewAdapter = new SectionedRecyclerViewAdapter();
        section = new T9Section(context, contactList, null);
        viewAdapter.addSection(section);
        recycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recycler.setAdapter(viewAdapter);
    }

    private void filterContacts(String query) {
        Disposable disposable = Observable.fromCallable(() -> filter(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    viewAdapter.notifyDataSetChanged();
                });
    }

    private boolean filter(String query) {
        section.filter(query);
        return true;
    }
}
