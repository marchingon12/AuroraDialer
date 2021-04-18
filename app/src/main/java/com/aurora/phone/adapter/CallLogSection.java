package com.aurora.phone.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.phone.CallLogActivity;
import com.aurora.phone.R;
import com.aurora.phone.entity.CallLog;
import com.aurora.phone.util.ColorUtil;
import com.aurora.phone.util.ImageUtil;
import com.aurora.phone.util.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class CallLogSection extends Section {

    private Context context;
    private String sectionTag;
    private List<CallLog> callLogList;
    private String sectionHeader;
    private ClickListener clickListener;

    public CallLogSection(Context context, String sectionTag, List<CallLog> callLogList, String sectionHeader, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_call_log)
                .headerResourceId(R.layout.item_header)
                .build());
        this.context = context;
        this.sectionTag = sectionTag;
        this.callLogList = callLogList;
        this.sectionHeader = sectionHeader;
        this.clickListener = clickListener;
    }

    public List<CallLog> getCallLogList() {
        return callLogList;
    }

    public void addAll(@NonNull final List<CallLog> newList) {
        callLogList.addAll(newList);
    }

    public void clear() {
        callLogList = new ArrayList<>();
    }

    public void remove(final int position) {
        callLogList.remove(position);
    }

    @Override
    public int getContentItemsTotal() {
        return callLogList.size();
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
        final int color = ImageUtil.getGradientAccentColor(position);
        CallLog callLog = callLogList.get(position);

        contentHolder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

        contentHolder.line1.setText(new StringBuilder()
                .append(getLabel(callLog))
                .append(StringUtils.SPACE)
                .append(callLog.getCount() > 1 ? "(" + callLog.getCount() + ")" : StringUtils.EMPTY));

        contentHolder.line2.setText(new StringBuilder()
                .append(callLog.getCachedName() == null
                        ? getGeoLabel(callLog)
                        : callLog.getNumber())
                .append(" • ")
                .append(Util.getDiffInDays(callLog.getDate()) > 1
                        ? Util.getDateFromMilli(callLog.getDate())
                        : Util.getTimeFromMilli(callLog.getDate())));

        contentHolder.tag.setColorFilter(ColorUtil.manipulateColor(color, 0.6f));
        contentHolder.item_action.setImageDrawable(getDrawable(callLog.getType()));
        contentHolder.img.setImageDrawable(ImageUtil.getGradientDrawable(position, GradientDrawable.OVAL));

        contentHolder.itemView.setOnClickListener(v -> {
            CallLogActivity.callLog = callLog;
            Intent intent = new Intent(context, CallLogActivity.class);
            //intent.putExtra("CONTACT_ID", contact.getContactId());

            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((AppCompatActivity) context);
            context.startActivity(intent, activityOptions.toBundle());
        });

        contentHolder.item_menu.setOnClickListener(v -> clickListener.onClickItem(callLog, sectionTag, position));
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
        void onClickItem(@NonNull CallLog callLog, String sectionTag, int sectionPosition);
    }

    public static class ContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.item_line2)
        TextView line2;
        @BindView(R.id.tag)
        ImageView tag;
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.item_action)
        ImageView item_action;
        @BindView(R.id.item_menu)
        ImageView item_menu;

        ContentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_header)
        TextView header;

        HeaderHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
