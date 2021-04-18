package com.aurora.phone.adapter.bundles;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.PhoneNumber;
import com.aurora.phone.R;
import com.aurora.phone.manager.CallManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class MobileSection extends Section {

    private Context context;
    private List<PhoneNumber> wrapperArrayList;
    private String header;

    public MobileSection(Context context, List<PhoneNumber> wrapperArrayList, String header) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_two_line_icon)
                .headerResourceId(R.layout.item_header)
                .build());
        this.context = context;
        this.wrapperArrayList = new ArrayList<>(new HashSet<>(wrapperArrayList));
        this.header = header;
    }

    @Override
    public int getContentItemsTotal() {
        return wrapperArrayList.size();
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
        final PhoneNumber phoneNumber = wrapperArrayList.get(position);
        contentHolder.line1.setText(phoneNumber.getLabelName());
        contentHolder.line2.setText(phoneNumber.getMainData());
        contentHolder.img.setImageDrawable(context.getDrawable(R.drawable.ic_call_alt));
        contentHolder.img.setOnClickListener(v -> {
            CallManager.call(context, phoneNumber.getMainData(), 1);
        });
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final HeaderHolder headerHolder = (HeaderHolder) holder;
        headerHolder.header.setText(header);
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.item_line2)
        TextView line2;
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
