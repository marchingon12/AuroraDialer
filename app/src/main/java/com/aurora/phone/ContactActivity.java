package com.aurora.phone;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.contact.interfaces.ContactsLiveData;
import com.aurora.contact.operations.ContactsGetterBuilder;
import com.aurora.phone.adapter.bundles.AddressSection;
import com.aurora.phone.adapter.bundles.EmailSection;
import com.aurora.phone.adapter.bundles.MobileSection;
import com.aurora.phone.util.ImageUtil;
import com.aurora.phone.util.ViewUtil;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactActivity extends AppCompatActivity {


    @BindView(R.id.menu)
    AppCompatImageView menu;
    @BindView(R.id.img)
    AppCompatImageView img;
    @BindView(R.id.top_layout)
    RelativeLayout topLayout;
    @BindView(R.id.line1)
    AppCompatTextView line1;
    @BindView(R.id.line2)
    AppCompatTextView line2;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtil.configureActivityLayout(this);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int contactId = intent.getIntExtra("CONTACT_ID", -1);
            if (contactId != -1) {
                fetchContactById(contactId);
            }

            String contactNumber = intent.getStringExtra("CONTACT_NUMBER");
            if (!StringUtils.isEmpty(contactNumber)) {
                fetchContactByNumber(contactNumber);
            }
        }
    }

    private void fetchContactById(int contactId) {
        ContactsLiveData contactsLiveData = new ContactsLiveData(this);
        contactsLiveData.setSelection(ContactsContract.CommonDataKinds.Phone._ID + " IS " + contactId);
        contactsLiveData.observe(this, cursor -> {
            buildContact(cursor);
        });
    }

    private void fetchContactByNumber(String contactNumber) {
        Contact contact = new ContactsGetterBuilder(this)
                .allFields()
                .withPhone(contactNumber)
                .firstOrNull();

        if (contact != null) {
            updateUI(contact);
            setupRecycler(contact);
        }
    }

    private void buildContact(Cursor cursor) {
        Disposable disposable = Observable.fromCallable(() -> Contact.getContact(this, cursor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(contact -> {
                    updateUI(contact);
                    setupRecycler(contact);
                });
    }

    private void updateUI(Contact contact) {
        img.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_circular_grow));
        topLayout.setBackground(ImageUtil.getGradientDrawable(contact.getContactId() % 5));
        line1.setText(contact.getCompositeName());
        if (StringUtils.isEmpty(contact.getNickName()))
            line2.setVisibility(View.GONE);
        else
            line2.setText(contact.getNickName());
    }

    private void setupRecycler(Contact contact) {
        SectionedRecyclerViewAdapter viewAdapter = new SectionedRecyclerViewAdapter();

        //Add all phone numbers
        if (contact.getPhoneList() != null)
            viewAdapter.addSection(new MobileSection(this, contact.getPhoneList(), "Mobile"));

        if (contact.getEmailList() != null)
            viewAdapter.addSection(new EmailSection(this, contact.getEmailList(), "Email"));

        if (contact.getAddressesList() != null)
            viewAdapter.addSection(new AddressSection(this, contact.getAddressesList(), "Address"));

        /*
        //Add all address
        List<ContactWrapper> addressWrapperList = getWrapperList(contacts, WrapperType.ADDRESS);
        if (!addressWrapperList.isEmpty())
            viewAdapter.addSection(new AddressSection(this, addressWrapperList, "Address"));

        //Add all websites
        List<ContactWrapper> websiteWrapperList = getWrapperList(contacts, WrapperType.WEBSITE);
        if (!addressWrapperList.isEmpty())
            viewAdapter.addSection(new WrapperSection(this, websiteWrapperList, "Website"));*/

        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recycler.setAdapter(viewAdapter);
    }

    @Override
    protected void onDestroy() {
        img.setAnimation(null);
        super.onDestroy();
    }
}
