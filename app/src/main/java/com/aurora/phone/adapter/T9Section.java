package com.aurora.phone.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.FilterableSection;
import com.aurora.phone.R;
import com.aurora.phone.util.ColorUtil;
import com.aurora.phone.util.ImageUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

import static android.graphics.Typeface.BOLD;

public class T9Section extends Section implements FilterableSection {

    private Context context;
    private List<Contact> contactList;
    private List<Contact> filteredList;
    private String searchQuery = null;
    private ClickListener clickListener;

    public T9Section(Context context, List<Contact> contactList, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_search)
                .build());
        this.context = context;
        this.contactList = contactList;
        this.filteredList = new ArrayList<>();
        this.clickListener = clickListener;
    }

    private String buildT9Pattern() {
        if (searchQuery.length() <= 3)
            return null;

        StringBuilder t9Pattern = new StringBuilder(StringUtils.EMPTY);
        for (int index = 0; index < searchQuery.length(); index++) {
            char c = searchQuery.charAt(index);
            switch (c) {
                case '2':
                    t9Pattern.append("[abc]");
                    break;
                case '3':
                    t9Pattern.append("[def]");
                    break;
                case '4':
                    t9Pattern.append("[ghi]");
                    break;
                case '5':
                    t9Pattern.append("[jkl]");
                    break;
                case '6':
                    t9Pattern.append("[mno]");
                    break;
                case '7':
                    t9Pattern.append("[pqrs]");
                    break;
                case '8':
                    t9Pattern.append("[tuv]");
                    break;
                case '9':
                    t9Pattern.append("[wxyz]");
                    break;
                default:
                    return "";
            }
        }
        return ".*" + t9Pattern.toString() + ".*";
    }

    @Override
    public int getContentItemsTotal() {
        return filteredList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ContentHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContentHolder contentHolder = (ContentHolder) holder;
        final int color = ImageUtil.getGradientAccentColor(position);
        final Contact contact = filteredList.get(position);

        contentHolder.line1.setText(contact.getCompositeName());
        contentHolder.line2.setText(contact.getPhoneNumber());

        contentHolder.tag.setText(ImageUtil.getNameTag(contact.getCompositeName()));
        contentHolder.tag.setTextColor(ColorUtil.manipulateColor(color, 0.5f));
        contentHolder.img.setImageDrawable(ImageUtil.getGradientDrawable(position, GradientDrawable.OVAL));

        if (!StringUtils.isEmpty(searchQuery)) {
            String compositeName = contact.getCompositeName();
            searchQuery = searchQuery.replaceAll("[^a-zA-Z0-9]", "");
            Pattern word = Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE);
            Matcher match = word.matcher(compositeName.toLowerCase());

            if (match.find()) {
                SpannableString spannable = new SpannableString(compositeName);
                spannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorAccent)),
                        match.start(),
                        match.end(),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannable.setSpan(new StyleSpan(BOLD),
                        match.start(),
                        match.end(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                contentHolder.line1.setText(spannable);
            }
        }

        if (!StringUtils.isEmpty(searchQuery) && !contact.getPhoneNumber().isEmpty()) {
            String phoneNumber = contact.getPhoneNumber();
            Pattern word = Pattern.compile(searchQuery);
            Matcher match = word.matcher(phoneNumber);

            if (match.find()) {
                SpannableString spannable = new SpannableString(phoneNumber);
                spannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorAccent)),
                        match.start(),
                        match.end(),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannable.setSpan(new StyleSpan(BOLD),
                        match.start(),
                        match.end(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                contentHolder.line2.setText(spannable);
            }
        }

        contentHolder.itemView.setOnClickListener(v -> {
            clickListener.onClickItem(contact, position);
        });
    }

    @Override
    public void filter(@NonNull String query) {
        searchQuery = query;

        if (TextUtils.isEmpty(query)) {
            filteredList.clear();
            filteredList.addAll(contactList);
            this.setVisible(true);
        } else {
            filteredList.clear();
            for (final Contact contact : contactList) {
                String compositeName = contact.getCompositeName();
                String number = contact.getPhoneNumber();

                if ((compositeName.toLowerCase()).contains(query.toLowerCase())) {
                    filteredList.add(contact);
                }

                if (number.contains(query)) {
                    filteredList.add(contact);
                }

                String t9Pattern = buildT9Pattern();
                if (t9Pattern == null)
                    continue;

                Pattern word = Pattern.compile(t9Pattern.toLowerCase());
                Matcher match = word.matcher(compositeName.toLowerCase());

                if (match.find()) {
                    filteredList.add(contact);
                }
            }
            this.setVisible(!filteredList.isEmpty());
        }
    }

    public interface ClickListener {
        void onClickItem(@NonNull Contact contact, int position);
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.item_line2)
        TextView line2;
        @BindView(R.id.tag)
        TextView tag;
        @BindView(R.id.img)
        ImageView img;

        ContentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
