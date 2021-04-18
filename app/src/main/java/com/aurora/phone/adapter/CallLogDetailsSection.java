package com.aurora.phone.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.phone.R;
import com.aurora.phone.entity.CallLog;
import com.aurora.phone.util.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class CallLogDetailsSection extends Section {

    private Context context;
    private List<CallLog> historyList;
    private String sectionHeader;
    private ClickListener clickListener;

    public CallLogDetailsSection(Context context, List<CallLog> historyList, String sectionHeader, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_call_log_history)
                .headerResourceId(R.layout.item_header)
                .build());
        this.context = context;
        this.historyList = historyList;
        this.sectionHeader = sectionHeader;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return historyList.size();
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
        CallLog callLog = historyList.get(position);

        contentHolder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

        contentHolder.line1.setText(callLog.getNumber());

        if (callLog.getType() == android.provider.CallLog.Calls.MISSED_TYPE) {
            contentHolder.line1.setTextColor(context.getColor(R.color.colorRed));
        }

        contentHolder.line2.setText(new StringBuilder()
                .append(getGeoLabel(callLog))
                .append(" • ")
                .append(Util.getDiffInDays(callLog.getDate()) > 1
                        ? Util.getDateFromMilli(callLog.getDate())
                        : Util.getTimeFromMilli(callLog.getDate())));

        contentHolder.img.setImageDrawable(getDrawable(callLog.getType()));

        contentHolder.itemView.setOnClickListener(v -> {

        });

        //contentHolder.item_menu.setOnClickListener(v -> clickListener.onClickItem(callLog, position));
    }

    private String getLabel(CallLog callLog) {
        if (!StringUtils.isEmpty(callLog.getCachedName()))
            return callLog.getCachedName();
        if (!StringUtils.isEmpty(callLog.getNumber()))
            return callLog.getNumber();
        else
            return "Unknown";
    }

    private String getGeoLabel(CallLog callLog) {
        if (!StringUtils.isEmpty(callLog.getGeoCodedLocation()))
            return callLog.getGeoCodedLocation();
        else
            return "Unknown";
    }

    private Drawable getDrawable(int type) {
        switch (type) {
            case android.provider.CallLog.Calls.MISSED_TYPE:
                return context.getResources().getDrawable(R.drawable.ic_call_missed, context.getTheme());
            case android.provider.CallLog.Calls.OUTGOING_TYPE:
                return context.getResources().getDrawable(R.drawable.ic_call_made, context.getTheme());
            case android.provider.CallLog.Calls.INCOMING_TYPE:
                return context.getResources().getDrawable(R.drawable.ic_call_received, context.getTheme());
            case android.provider.CallLog.Calls.REJECTED_TYPE:
            case android.provider.CallLog.Calls.BLOCKED_TYPE:
                return context.getResources().getDrawable(R.drawable.ic_call_rejected, context.getTheme());
            default:
                return context.getResources().getDrawable(R.drawable.ic_call_missed, context.getTheme());
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final HeaderHolder headerHolder = (HeaderHolder) holder;
        headerHolder.header.setText(sectionHeader);
    }

    public interface ClickListener {
        void onClickItem(@NonNull CallLog callLog, int position);

        void onLongClickItem(@NonNull CallLog callLog, int position);
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.item_line2)
        TextView line2;
        @BindView(R.id.item_menu)
        ImageView item_menu;

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
