package com.xu.googleplay.ui.fragment;


import android.view.View;

import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.http.protocol.AppProtocol;
import com.xu.googleplay.ui.adapter.MyBaseAdapter;
import com.xu.googleplay.ui.holder.AppHolder;
import com.xu.googleplay.ui.holder.BaseHolder;
import com.xu.googleplay.ui.view.LoadingPage.ResultState;
import com.xu.googleplay.ui.view.MyListView;
import com.xu.googleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 应用
 */
public class AppFragment extends BaseFragment {

	//需要data，就在全局声明一下，因为数据都一样，可以用AppInfo
	private ArrayList<AppInfo> data;
	//只有成功才走此方法，实现了基类的方法
	@Override
	public View onCreateSuccessView() {
		MyListView view = new MyListView(UIUtils.getContext());
		view.setAdapter(new AppAdapter(data));
		return view;
	}

	//然后加载网络的数据
	@Override
	public ResultState onLoad() {
		//首页弄的是HomeProtocol，这里就新建AppProtocol，加载这里就新建AppProtocol的数据
		AppProtocol protocol = new AppProtocol();
		//把0穿过去，第一页数据，返回的值传给全局的data
		data = protocol.getData(0);
		//检测是否合法数据
		return check(data);
	}

	//数据填充，写一个自己的Adapter
	class AppAdapter extends MyBaseAdapter<AppInfo> {

		//实现构造方法
		public AppAdapter(ArrayList<AppInfo> data) {
			super(data);
		}

		//需要自己写一个AppHolder
		@Override
		public BaseHolder<AppInfo> getHolder(int position) {
			return new AppHolder();
		}

		//加载更多数据
		//逻辑和加载网络的数据是一样的
		@Override
		public ArrayList<AppInfo> onLoadMore() {
			AppProtocol protocol = new AppProtocol();
			ArrayList<AppInfo> moreData = protocol.getData(getListSize());
			return moreData;
		}

	}
}
