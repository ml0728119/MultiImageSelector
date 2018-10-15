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

public class SubmitButton extends android.support.v7.widget.AppCompatButton {
	MultiImageSelector.MultiImageControl multiImageControl= MultiImageSelector.multiImageControl;
	LinkedHashSet<String> resultList;
	public SubmitButton(Context context) {
		super(context);
	}

	public SubmitButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		 resultList =multiImageControl .getChooseValue();
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (resultList != null && resultList.size() > 0) {
					MultiImageSelector.multiImageControl.commit((Activity) getContext());
				}
				((Activity) getContext()).finish();
			}
		});
	}

	public void updateDoneText() {

		int size = 0;
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
