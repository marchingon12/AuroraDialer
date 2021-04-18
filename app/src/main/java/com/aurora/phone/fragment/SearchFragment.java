package com.aurora.phone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.FilterableSection;
import com.aurora.phone.R;
import com.aurora.phone.adapter.SearchSection;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class SearchFragment extends Fragment {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.txt_input_search)
    TextInputEditText searchInput;

    private Context context;
    private SectionedRecyclerViewAdapter viewAdapter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupSearchBar();
       /* ContactsModel contactsModel = ViewModelProviders.of(this).get(ContactsModel.class);
        contactsModel.getContactList().observe(getViewLifecycleOwner(), contactList -> {
            setupRecycler(contactList);
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        //Util.toggleSoftInput(context, true);
        searchInput.requestFocus();
    }

    @Override
    public void onDestroy() {
        //Util.toggleSoftInput(context, false);
        super.onDestroy();
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
}
