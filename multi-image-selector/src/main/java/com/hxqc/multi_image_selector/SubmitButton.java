package com.hxqc.multi_image_selector;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedHashSet;

/**
 * Created 胡俊杰
 * 2016/12/8.
 * Todo:
 */

public class SubmitButton extends androidx.appcompat.widget.AppCompatButton {
//    MultiImageSelector.MultiImageControl multiImageControl;


    public SubmitButton(Context context) {
        super(context);
    }

    public SubmitButton(Context context, AttributeSet attrs) {
        super(context, attrs);


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = null;
                if (getContext() instanceof Activity) {
                    activity = (Activity) getContext();
                } else {
                    activity = ((Activity) ((SubmitButton) v).context);
                }
                LinkedHashSet<String> resultList = MultiImageSelector.multiImageControl.getChooseValue();
                if (resultList != null && resultList.size() > 0) {
                    if (activity != null)
                        MultiImageSelector.multiImageControl.commit(activity);
                }
                if (activity != null)
                    activity.finish();
            }
        });
    }

    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void updateDoneText() {

        int size = 0;
        MultiImageSelector.MultiImageControl multiImageControl = MultiImageSelector.multiImageControl;
        LinkedHashSet<String> resultList = MultiImageSelector.multiImageControl.getChooseValue();
        switch (multiImageControl.getMode()) {
            case MultiImageSelector.MultiImageControl.MODE_MULTI:
                if (resultList == null || resultList.size() <= 0) {
                    setText(R.string.mis_action_done);
                    setEnabled(false);
                } else {
                    size = resultList.size();
                    setEnabled(true);
                }
                setText(getContext().getString(R.string.mis_action_button_string,
                        getContext().getString(R.string.mis_action_done), size, multiImageControl.getMaxCount()));
                break;
            case MultiImageSelector.MultiImageControl.MODE_SINGLE:
                setText(R.string.mis_action_done);
                if (resultList == null || resultList.size() <= 0) {
                    setEnabled(false);
                } else {
                    setEnabled(true);
                }
                break;
        }
    }
}
