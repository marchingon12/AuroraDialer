package com.aurora.phone.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.phone.R;
import com.aurora.phone.entity.Favourite;
import com.aurora.phone.util.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class SuggestionSection extends Section {

    private Context context;
    private List<Favourite> callHistoryList;
    private String suggestionHeader;

    public SuggestionSection(Context context, List<Favourite> callHistoryList, String suggestionHeader) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_suggestion)
                .emptyResourceId(R.layout.item_empty_suggestion)
                .build());
        this.context = context;
        this.callHistoryList = callHistoryList;
        this.suggestionHeader = suggestionHeader;

        if (callHistoryList.isEmpty())
            setState(State.EMPTY);
    }

    @Override
    public int getContentItemsTotal() {
        return callHistoryList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ContentHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getEmptyViewHolder(View view) {
        return new EmptyHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContentHolder contentHolder = (ContentHolder) holder;
        Favourite favourite = callHistoryList.get(position);
        contentHolder.line1.setText(favourite.getContact().getCompositeName());
        contentHolder.item_image.setImageDrawable(ImageUtil.getGradientDrawable(position, GradientDrawable.OVAL));
    }

    @Override
    public void onBindEmptyViewHolder(RecyclerView.ViewHolder holder) {

    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.img)
        ImageView item_image;

        ContentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class EmptyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line1)
        TextView header;

        EmptyHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
