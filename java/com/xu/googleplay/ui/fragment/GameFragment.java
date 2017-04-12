package com.xu.googleplay.ui.fragment;


import android.view.View;
import android.widget.TextView;

import com.xu.googleplay.ui.view.LoadingPage.ResultState;
import com.xu.googleplay.utils.UIUtils;

/**
 * 游戏
 */
public class GameFragment extends BaseFragment {
    //只有成功才走此方法，实现了基类的方法
	@Override
	public View onCreateSuccessView() {
		TextView view = new TextView(UIUtils.getContext());
		view.setText("GameFragment");
		return view;
	}

	@Override
	public ResultState onLoad() {
		return ResultState.STATE_SUCCESS;
	}
}
