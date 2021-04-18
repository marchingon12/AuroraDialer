package com.aurora.phone.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.contact.interfaces.ContactsLiveData;
import com.aurora.phone.LoadContactsUseCase;
import com.aurora.phone.R;
import com.aurora.phone.adapter.ContactsSection;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactsFragment extends Fragment implements ContactsSection.ClickListener {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ContactsLiveData contactsLiveData = new ContactsLiveData(requireContext());
        contactsLiveData.observe(getViewLifecycleOwner(), cursor -> {
            buildContacts(cursor);
        });
    }

    private void buildContacts(Cursor cursor) {
        Disposable disposable = Observable.fromCallable(() -> Contact.getLiteContactList(requireContext(), cursor))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    setupRecycler(contacts);
                }, err -> {

                });
        compositeDisposable.add(disposable);
    }

    private void setupRecycler(List<Contact> contactList) {

        SectionedRecyclerViewAdapter viewAdapter = new SectionedRecyclerViewAdapter();
        final Map<String, List<Contact>> contactsMap = new LoadContactsUseCase(contactList).execute(requireContext());
        for (final Map.Entry<String, List<Contact>> entry : contactsMap.entrySet()) {
            if (entry.getValue().size() > 0) {
                viewAdapter.addSection(entry.getKey(), new ContactsSection(requireContext(), entry.getValue(), entry.getKey(), this));
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(viewAdapter);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onClickItem(@NonNull Contact contact) {/*
        Bundle bundle = new Bundle();
        bundle.putLong("CONTACT_ID", contact.getContactId());
        NavHostFragment.findNavController(this).saveState();
        NavHostFragment.findNavController(this).navigate(R.data.contacts_to_details, bundle);*/
    }

    @Override
    public void onLongClickItem(@NonNull Contact contact) {
        /*ContactsMenu contactsMenu = ContactsMenu.getInstance();
        contactsMenu.setCallLog(contact);
        contactsMenu.show(getChildFragmentManager(), "MENU_CONTACT");*/
    }
}
