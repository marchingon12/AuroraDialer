package com.aurora.phone.sheet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aurora.phone.R;
import com.aurora.phone.entity.CallLog;
import com.aurora.phone.util.ContextUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogMenu extends BottomSheetDialogFragment {

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private Context context;
    private String sectionTag;
    private int sectionPosition;
    private CallLog callLog;

    public static CallLogMenu getInstance() {
        return new CallLogMenu();
    }

    public String getSectionTag() {
        return sectionTag;
    }

    public void setSectionTag(String sectionTag) {
        this.sectionTag = sectionTag;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }

    public void setCallLog(CallLog callLog) {
        this.callLog = callLog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_bottom, container, false);
        ButterKnife.bind(this, view);
        navigationView.inflateMenu(R.menu.menu_call_log);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_call_details:
                case R.id.action_copy:
                    ContextUtil.toastLong(context, "Coming soon..");
                    break;
                case R.id.action_delete:
                    boolean result = CallLog.deleteCallLog(context, callLog);
                    //if (result)
                    //AuroraApplication.rxNotify(new Event(Event.SubType.LOG_DELETE, sectionTag, sectionPosition));
                    break;
            }
            dismissAllowingStateLoss();
            return false;
        });
    }
}
