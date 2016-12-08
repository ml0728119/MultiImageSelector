package me.nereo.multi_image_selector.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import java.util.LinkedHashSet;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.R;

/**
 * Created 胡俊杰
 * 2016/12/8.
 * Todo:
 */

public class SubmitButton extends Button {

	public SubmitButton(Context context) {
		super(context);
	}

	public SubmitButton(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public void updateDoneText() {
		LinkedHashSet<String> resultList = MultiImageSelector.getSingleton().getChooseValue();

		MultiImageSelector multiImageSelector = MultiImageSelector.getSingleton();
		int size = 0;
		switch (multiImageSelector.getMode()) {
			case MultiImageSelector.MODE_MULTI:
				if (resultList == null || resultList.size() <= 0) {
					setText(R.string.mis_action_done);
					setEnabled(false);
				} else {
					size = resultList.size();
					setEnabled(true);
				}

				setText(getContext().getString(R.string.mis_action_button_string,
						getContext().getString(R.string.mis_action_done), size, multiImageSelector.getMaxCount()));
				break;
			case MultiImageSelector.MODE_SINGLE:
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
