package com.aurora.phone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.phone.adapter.CallLogDetailsSection;
import com.aurora.phone.entity.CallLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class CallLogActivity extends AppCompatActivity {

    public static CallLog callLog;

    @BindView(R.id.action_backward)
    AppCompatImageView actionBackward;
    @BindView(R.id.action_text)
    AppCompatTextView actionText;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.img)
    AppCompatImageView img;
    @BindView(R.id.line1)
    AppCompatTextView line1;
    @BindView(R.id.line2)
    AppCompatTextView line2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_history_details);
        ButterKnife.bind(this);
        onNewIntent(getIntent());

        if (callLog == null) {
            finishAfterTransition();
            return;
        }

        updateUI(callLog);
        setupRecycler(callLog);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int contactId = intent.getIntExtra("CONTACT_ID", -1);
            String contactNumber = intent.getStringExtra("CONTACT_NUMBER");

        }
    }

    private void updateUI(CallLog callLog) {
        actionText.setText("Call details");
        img.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_circular_grow));
        line1.setText(callLog.getCachedName());
        line2.setText(callLog.getNumber());
    }

    private void setupRecycler(CallLog callLog) {
        List<CallLog> callLogList = new ArrayList<>();
        callLogList.add(callLog);
        callLogList.addAll(callLog.getRelatedLogs());
        SectionedRecyclerViewAdapter viewAdapter = new SectionedRecyclerViewAdapter();
        viewAdapter.addSection(new CallLogDetailsSection(this, callLogList, "Recent calls", null));
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recycler.setAdapter(viewAdapter);
    }

    @Override
    protected void onDestroy() {
        img.setAnimation(null);
        callLog = null;
        super.onDestroy();
    }
}
