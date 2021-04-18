package com.aurora.phone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.R;
import com.aurora.phone.WrapperType;
import com.aurora.phone.entity.ContactWrapper;
import com.aurora.phone.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.disposables.CompositeDisposable;

public class ContactsDetailFragment extends Fragment {

    @BindView(R.id.top_layout)
    RelativeLayout topLayout;
    @BindView(R.id.line1)
    TextView txtLine1;
    @BindView(R.id.line2)
    TextView txtLine2;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.menu)
    AppCompatImageView menu;
    @BindView(R.id.img)
    AppCompatImageView img;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;

    private Context context;
    private long contactId;
    private CompositeDisposable disposable = new CompositeDisposable();

    public static ContactsDetailFragment newInstance() {
        return new ContactsDetailFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            contactId = bundle.getLong("CONTACT_ID", 0);
            fetchContact(contactId);
        }
    }

    private void fetchContact(long contactId) {
        /*disposable.add(
                Observable.fromCallable(() -> new FetchTask(context)
                        .getContactsById(contactId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(contacts -> {
                            if (contacts.isEmpty()) {

                            } else {
                                updateUI(contacts);
                            }
                        })
        );*/
    }

    private void updateUI(List<Contact> contacts) {
        Contact contact = contacts.get(0);
        int color = ImageUtil.getGradientAccentColor(contact.getContactId() % 5);
        getActivity().getWindow().setStatusBarColor(color);
        topLayout.setBackground(ImageUtil.getGradientDrawable(contact.getContactId()));
        txtLine1.setText(contact.getCompositeName());
        txtLine2.setText(contact.getNickName());
        setupRecycler(contacts);
    }

    private void setupRecycler(List<Contact> contacts) {
        SectionedRecyclerViewAdapter viewAdapter = new SectionedRecyclerViewAdapter();

        /*//Add all phone numbers
        List<ContactWrapper> phoneWrapperList = getWrapperList(contacts, WrapperType.MOBILE);
        if (!phoneWrapperList.isEmpty())
            viewAdapter.addSection(new MobileSection(context, phoneWrapperList, "Mobile"));

        //Add all emails
        List<ContactWrapper> emailWrapperList = getWrapperList(contacts, WrapperType.EMAIL);
        if (!emailWrapperList.isEmpty())
            viewAdapter.addSection(new EmailSection(context, emailWrapperList, "Email"));

        //Add all address
        List<ContactWrapper> addressWrapperList = getWrapperList(contacts, WrapperType.ADDRESS);
        if (!addressWrapperList.isEmpty())
            viewAdapter.addSection(new AddressSection(context, addressWrapperList, "Address"));

        //Add all websites
        List<ContactWrapper> websiteWrapperList = getWrapperList(contacts, WrapperType.WEBSITE);
        if (!addressWrapperList.isEmpty())
            viewAdapter.addSection(new WrapperSection(context, websiteWrapperList, "Website"));*/

        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(viewAdapter);
    }

    private List<ContactWrapper> getWrapperList(List<Contact> contactList, WrapperType type) {
        List<ContactWrapper> wrapperArrayList = new ArrayList<>();
        /*for (Contact contact : contactList) {
            switch (type) {
                case MOBILE:
                    wrapperArrayList.addAll(contact.getPhoneList());
                    break;
                case EMAIL:
                    wrapperArrayList.addAll(contact.getEmailList());
                    break;
                case WEBSITE:
                    wrapperArrayList.addAll(contact.getWebsiteList());
                    break;
                case ADDRESS:
                    wrapperArrayList.addAll(contact.getAddressList());
                    break;
            }
        }*/
        return wrapperArrayList;
    }
}
