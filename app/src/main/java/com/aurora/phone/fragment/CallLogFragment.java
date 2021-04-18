package com.aurora.phone.fragment;

import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.contact.interfaces.CallLogsLiveData;
import com.aurora.phone.LoadRecentLogsDate;
import com.aurora.phone.R;
import com.aurora.phone.adapter.CallLogSection;
import com.aurora.phone.entity.CallLog;
import com.aurora.phone.manager.CallManager;
import com.aurora.phone.sheet.CallLogMenu;
import com.aurora.phone.util.ImageUtil;
import com.aurora.phone.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CallLogFragment extends Fragment implements CallLogSection.ClickListener {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ItemTouchHelper itemTouchHelper = null;
    private SectionedRecyclerViewAdapter adapter = null;

    public static CallLogFragment newInstance() {
        return new CallLogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CallLogsLiveData callLogsLiveData = new CallLogsLiveData(requireContext());
        callLogsLiveData.observe(getViewLifecycleOwner(), cursor -> {
            buildCallLogs(cursor);
        });
    }

    private void dispatchUpdatesToCallLogs(List<CallLog> callLogList) {
        Log.e("Lund");
        callLogList = getFilteredLogs(callLogList);
        final Map<String, List<CallLog>> contactsMap = new LoadRecentLogsDate(callLogList).execute();
        for (final Map.Entry<String, List<CallLog>> entry : contactsMap.entrySet()) {

            if (adapter.getSection(entry.getKey()) == null && entry.getValue().size() > 0) {
                adapter.addSection(0, entry.getKey(), new CallLogSection(requireContext(),
                        entry.getKey(),
                        entry.getValue(),
                        entry.getKey(),
                        this));
                continue;
            }

            if (entry.getValue().size() == 0) {
                adapter.removeSection(entry.getKey());
                adapter.notifyDataSetChanged();
            } else {
                final CallLogSection section = (CallLogSection) adapter.getSection(entry.getKey());
                final SectionAdapter sectionAdapter = adapter.getAdapterForSection(entry.getKey());

                if (!section.getCallLogList().equals(entry.getValue())) {


                    final List<CallLog> oldList = section.getCallLogList();
                    final List<CallLog> newList = entry.getValue();


                    int diffIndex = 0;

                    for (int index = 0; index < entry.getValue().size(); index++) {
                        if (oldList.get(index).getId() != newList.get(index).getId()) {
                            diffIndex = index;
                            break;
                        }
                    }

                    section.clear();
                    section.addAll(newList);
                    if (oldList.size() > newList.size())
                        sectionAdapter.notifyItemRemoved(diffIndex);
                    else
                        sectionAdapter.notifyItemInserted(diffIndex);
                }
            }
        }
    }

    private void buildCallLogs(Cursor cursor) {
        Observable.just(CallLog.getCallLogs(cursor))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(callLogs -> {
                    setupRecycler(callLogs);
                })
                .doOnError(throwable -> {
                    Log.e(throwable.getMessage());
                })
                .subscribe();
    }

    private void setupRecycler(List<CallLog> callLogList) {
        callLogList = getFilteredLogs(callLogList);
        adapter = new SectionedRecyclerViewAdapter();

        final Map<String, List<CallLog>> contactsMap = new LoadRecentLogsDate(callLogList).execute();
        for (final Map.Entry<String, List<CallLog>> entry : contactsMap.entrySet()) {
            if (entry.getValue().size() > 0) {
                adapter.addSection(entry.getKey(), new CallLogSection(requireContext(),
                        entry.getKey(),
                        entry.getValue(),
                        entry.getKey(),
                        this));
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        itemTouchHelper = new ItemTouchHelper(getSimpleCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private ItemTouchHelper.SimpleCallback getSimpleCallback() {
        return new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            private GradientDrawable background = ImageUtil.getGradientDrawable(3);

            private Drawable drawableCall = ContextCompat.getDrawable(requireContext(), R.drawable.ic_call);
            private Drawable drawableMessage = ContextCompat.getDrawable(requireContext(), R.drawable.ic_message);

            private int intrinsicWidth = drawableCall.getIntrinsicWidth();
            private int intrinsicHeight = drawableCall.getIntrinsicHeight();

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Section section = adapter.getSectionForPosition(position);
                int sectionPosition = adapter.getPositionInSection(position);

                if (direction == ItemTouchHelper.LEFT && section instanceof CallLogSection) {
                    CallLogSection callLogSection = (CallLogSection) section;
                    CallManager.call(requireContext(), callLogSection.getCallLogList().get(sectionPosition).getNumber(), 1);

                } else if (direction == ItemTouchHelper.RIGHT && section instanceof CallLogSection) {
                    CallLogSection callLogSection = (CallLogSection) section;
                    //TODO: Add Send message module call
                }
                dirtyUpdateItemTouchHelper();
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (viewHolder instanceof CallLogSection.HeaderHolder) {
                    super.onChildDraw(canvas, recyclerView, viewHolder, 0, 0, actionState, isCurrentlyActive);
                    return;
                }

                //dX = dX / 2;
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;


                if (dX > 0) {
                    background = ImageUtil.getSwipeGradientDrawable(0);
                    background.setBounds(
                            itemView.getLeft(),
                            itemView.getTop(),
                            itemView.getLeft() + ((int) dX),
                            itemView.getBottom());
                } else if (dX < 0) {
                    background = ImageUtil.getSwipeGradientDrawable(1);
                    background.setBounds(
                            itemView.getRight() + ((int) dX),
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                int itemHeight = itemView.getBottom() - itemView.getTop();
                int drawableMargin = (itemHeight - intrinsicHeight) / 2;

                int top = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int bottom = top + intrinsicHeight;


                int left = itemView.getRight() - drawableMargin - intrinsicWidth;
                int right = itemView.getRight() - drawableMargin;

                background.draw(canvas);

                drawableCall.setBounds(left, top, right, bottom);
                drawableCall.draw(canvas);

                left = itemView.getLeft() + drawableMargin;
                right = itemView.getLeft() + drawableMargin + intrinsicWidth;

                drawableMessage.setBounds(left, top, right, bottom);
                drawableMessage.draw(canvas);
            }
        };
    }

    private void dirtyUpdateItemTouchHelper() {
        itemTouchHelper.attachToRecyclerView(null);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private List<CallLog> getFilteredLogs(List<CallLog> callLogList) {
        CallLog tempLog = callLogList.get(0);
        List<CallLog> filteredCallLogs = new ArrayList<>();
        List<CallLog> relatedCallLogs = new ArrayList<>();

        int count = 1;

        for (int i = 1; i < callLogList.size(); i++) {
            CallLog refLog = callLogList.get(i);
            if (!tempLog.getNumber().equals(refLog.getNumber())) {
                tempLog.setCount(count);
                tempLog.setRelatedLogs(relatedCallLogs);

                filteredCallLogs.add(tempLog);

                relatedCallLogs = new ArrayList<>();
                tempLog = refLog;
                count = 1;
            } else {
                relatedCallLogs.add(refLog);
                count++;
            }
        }
        return filteredCallLogs;
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onClickItem(@NonNull CallLog callLog, String sectionTag, int sectionPosition) {
        CallLogMenu callLogMenu = CallLogMenu.getInstance();
        callLogMenu.setSectionTag(sectionTag);
        callLogMenu.setSectionPosition(sectionPosition);
        callLogMenu.setCallLog(callLog);
        callLogMenu.show(getChildFragmentManager(), "FAVOURITE_MENU");
    }
}
