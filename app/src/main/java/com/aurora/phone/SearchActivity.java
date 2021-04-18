package com.aurora.phone;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.contact.interfaces.SearchLiveData;
import com.aurora.phone.adapter.SearchSection;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.txt_input_search)
    TextInputEditText searchInput;

    private SectionedRecyclerViewAdapter viewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        SearchLiveData searchLiveData = new SearchLiveData(this);
        searchLiveData.observe(this, cursor -> {
            buildContacts(cursor);
        });
    }

    private void buildContacts(Cursor cursor) {
        Disposable disposable = Observable.fromCallable(() -> Contact.getSearchContactList(this, cursor))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    setupSearchBar();
                    setupRecycler(contacts);
                }, err -> {

                });
    }

    private void setupSearchBar() {
        searchInput.addTextChangedListener(new TextWatcher() {
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

    private void filterContacts(String query) {
        for (Section section : viewAdapter.getCopyOfSectionsMap().values()) {
            if (section instanceof SearchSection) {
                ((FilterableSection) section).filter(query);
            }
        }
        viewAdapter.notifyDataSetChanged();
    }

    private void setupRecycler(List<Contact> contactList) {
        viewAdapter = new SectionedRecyclerViewAdapter();
        viewAdapter.addSection(new SearchSection(this, contactList, "All contacts"));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(viewAdapter);
    }
}
