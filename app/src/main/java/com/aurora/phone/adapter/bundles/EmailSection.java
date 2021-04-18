package com.aurora.phone.adapter.bundles;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Email;
import com.aurora.phone.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class EmailSection extends Section {

    private Context context;
    private List<Email> wrapperArrayList;
    private String header;

    public EmailSection(Context context, List<Email> wrapperArrayList, String header) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_two_line_icon)
                .headerResourceId(R.layout.item_header)
                .build());
        this.context = context;
        this.wrapperArrayList = wrapperArrayList;
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
        final Email contactWrapper = wrapperArrayList.get(position);
        contentHolder.line1.setText(contactWrapper.getLabelName());
        contentHolder.line2.setText(contactWrapper.getMainData());
        contentHolder.img.setImageDrawable(context.getDrawable(R.drawable.ic_email));
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
