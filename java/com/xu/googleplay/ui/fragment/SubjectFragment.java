package com.xu.googleplay.ui.fragment;


import android.view.View;

import com.xu.googleplay.domain.SubjectInfo;
import com.xu.googleplay.http.protocol.SubjectProtocol;
import com.xu.googleplay.ui.adapter.MyBaseAdapter;
import com.xu.googleplay.ui.holder.BaseHolder;
import com.xu.googleplay.ui.holder.SubjectHolder;
import com.xu.googleplay.ui.view.LoadingPage.ResultState;
import com.xu.googleplay.ui.view.MyListView;
import com.xu.googleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 专题
 */
public class SubjectFragment extends BaseFragment {

	private ArrayList<SubjectInfo> data;

    //只有成功才走此方法，实现了基类的方法
	@Override
	public View onCreateSuccessView() {
		MyListView view = new MyListView(UIUtils.getContext());
		view.setAdapter(new SubjectAdapter(data));
		return view;
	}

	//请求网络数据
	@Override
	public ResultState onLoad() {
		SubjectProtocol protocol = new SubjectProtocol();
		//获取的第一页数据，data返回出去
		data = protocol.getData(0);
		//检查数据
		return check(data);
	}

	//填充数据，这里的泛型的类型是item的对象，只是一个SubjectInfo
	class SubjectAdapter extends MyBaseAdapter<SubjectInfo> {
		//实现3个方法
		public SubjectAdapter(ArrayList<SubjectInfo> data) {
			super(data);
		}

		@Override
		public BaseHolder<SubjectInfo> getHolder(int position) {
			return new SubjectHolder();
		}

		@Override
		public ArrayList<SubjectInfo> onLoadMore() {
			SubjectProtocol protocol = new SubjectProtocol();
			//这个是更多数据，不是全局数据，所有不要和外面的data搞混了
			ArrayList<SubjectInfo> moreData = protocol.getData(getListSize());
			return moreData;
		}

	}

}
