package com.aurora.phone.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.ContactActivity;
import com.aurora.phone.MainActivity;
import com.aurora.phone.R;
import com.aurora.phone.util.ColorUtil;
import com.aurora.phone.util.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class ContactsSection extends Section {

    private Context context;
    private List<Contact> contactList;
    private String contactsHeader;
    private ClickListener clickListener;

    public ContactsSection(Context context, List<Contact> contactList, String contactsHeader, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_contact)
                .headerResourceId(R.layout.item_header)
                .build());
        this.context = context;
        this.contactList = contactList;
        this.contactsHeader = contactsHeader;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return contactList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ContentHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContentHolder contentHolder = (ContentHolder) holder;
        final Contact contact = contactList.get(position);
        final int color = ImageUtil.getGradientAccentColor(contact.getContactId() % 5);

        //contentHolder.img.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
        contentHolder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

        contentHolder.line1.setText(contact.getCompositeName());
        contentHolder.tag.setText(ImageUtil.getNameTag(contact.getCompositeName()));
        contentHolder.tag.setTextColor(ColorUtil.manipulateColor(color, 0.5f));
        contentHolder.img.setImageDrawable(ImageUtil.getGradientDrawable(contact.getContactId() % 5, GradientDrawable.OVAL));

        contentHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ContactActivity.class);
            intent.putExtra("CONTACT_ID", contact.getContactId());

            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((AppCompatActivity) context);
            context.startActivity(intent, activityOptions.toBundle());
        });
        contentHolder.itemView.setOnLongClickListener(v -> {
            clickListener.onLongClickItem(contact);
            return false;
        });
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final HeaderHolder headerHolder = (HeaderHolder) holder;
        headerHolder.header.setText(contactsHeader);
    }

    public interface ClickListener {
        void onClickItem(@NonNull Contact contact);

        void onLongClickItem(@NonNull Contact contact);
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.tag)
        TextView tag;
        @BindView(R.id.img)
        ImageView img;

        ContentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_header)
        TextView header;

        HeaderHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
