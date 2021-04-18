package com.aurora.phone.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.entity.Contact;
import com.aurora.phone.R;
import com.aurora.phone.util.ColorUtil;
import com.aurora.phone.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class FavouriteSection extends Section {

    private Context context;
    private ClickListener clickListener;
    private List<Contact> favouriteList;

    public FavouriteSection(Context context, List<Contact> favouriteList, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_favourite)
                .emptyResourceId(R.layout.item_empty_favourites)
                .build());
        this.context = context;
        this.favouriteList = favouriteList;
        this.clickListener = clickListener;

        if (favouriteList.isEmpty())
            setState(State.EMPTY);
    }

    public void update(List<Contact> favouriteList) {
        this.favouriteList = favouriteList;
    }

    @Override
    public int getContentItemsTotal() {
        return favouriteList.size();
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
        final Contact favourite = favouriteList.get(position);
        final int color = ImageUtil.getGradientAccentColor(position);

        contentHolder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

        contentHolder.itemView.setBackground(ImageUtil.getGradientDrawable(position, GradientDrawable.RECTANGLE));
        contentHolder.line1.setText(favourite.getCompositeName());
        contentHolder.line1.setTextColor(ColorUtil.manipulateColor(color, 0.5f));

        contentHolder.item_menu.setOnClickListener(v -> clickListener.onClickItem(favourite, position));
        contentHolder.item_menu.setOnLongClickListener(v -> {
            clickListener.onClickItem(favourite, position);
            return false;
        });
    }

    @Override
    public void onBindEmptyViewHolder(RecyclerView.ViewHolder holder) {

    }

    public interface ClickListener {
        void onClickItem(@NonNull Contact favourite, int position);

        void onLongClickItem(@NonNull Contact favourite, int position);
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.img)
        ImageView item_image;
        @BindView(R.id.item_menu)
        ImageView item_menu;

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
