package com.xu.googleplay.ui.fragment;


import android.view.View;

import com.xu.googleplay.domain.CategoryInfo;
import com.xu.googleplay.http.protocol.CategoryProtocol;
import com.xu.googleplay.ui.adapter.MyBaseAdapter;
import com.xu.googleplay.ui.holder.BaseHolder;
import com.xu.googleplay.ui.holder.CategoryHolder;
import com.xu.googleplay.ui.holder.TitleHolder;
import com.xu.googleplay.ui.view.LoadingPage.ResultState;
import com.xu.googleplay.ui.view.MyListView;
import com.xu.googleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 分类
 *
 * 标题和信息的listView的界面，需要arrayList的集合，里面要塞对象的，标题和信息是一个对象
 */
public class CategoryFragment extends BaseFragment {
	private ArrayList<CategoryInfo> data;
    //只有成功才走此方法，实现了基类的方法
	@Override
	public View onCreateSuccessView() {
		//初始化ListView
		MyListView view = new MyListView(UIUtils.getContext());
		view.setAdapter(new CategoryAdapter(data));
		return view;
	}

	@Override
	public ResultState onLoad() {
		CategoryProtocol protocol = new CategoryProtocol();
		data = protocol.getData(0);
		return check(data);
	}

	//这里的对象不是一个集合，是某个item对应的对象
	/*
	* 没有分页效果，不需要加载更多，要告诉CategoryAdapter这里是不需要加载更多了
	*
	* 界面有2中布局类型，加上之前隐藏的一袭红3种布局类型，要重写getViewTypeCount
	*
	* 因为多了一种类型，所有重写getInnerType
	* */
	class CategoryAdapter extends MyBaseAdapter<CategoryInfo> {

		public CategoryAdapter(ArrayList<CategoryInfo> data) {
			super(data);
		}

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;// 在原来基础上增加一种标题类型
		}

		//因为多了一种类型，所有重写getInnerType,但是要把当时的position传过来，
		@Override
		public int getInnerType(int position) {
			// 判断是标题类型还是普通分类类型
			CategoryInfo info = data.get(position);

			//判断是不是标题
			if (info.isTitle) {
				// 返回标题类型
				return super.getInnerType(position) + 1;// 原来类型基础上加1;
				// 注意:将TYPE_NORMAL修改为1;
			} else {
				//因为之前已经定义过了，直接返回默认的就可以，就是父类的实现
				// 返回普通类型
				return super.getInnerType(position);
			}
		}

		//不能直接new一个CategoryHolder，因为只能刷新一个页面，里面有2个完全不一样的布局，需要2种Holder，标题和普通
		@Override
		public BaseHolder<CategoryInfo> getHolder(int position) {
			// 根据位置来判断是标题类型还是普通分类类型, 来返回不同的holder
			CategoryInfo info = data.get(position);
			//有了position就可以判断是不是一个标题
			if (info.isTitle) {
				return new TitleHolder();
			} else {
				return new CategoryHolder();
			}
		}

		 //没有分页效果，不需要加载更多，要告诉CategoryAdapter这里是不需要加载更多了
		//重写这个方法，没有跟多数据就可以了
		@Override
		public boolean hasMore() {
			return false;// 没有更多数据, 需要隐藏加载更多的布局
		}


		//返回空就可以了，因为这类已经判断了
		@Override
		public ArrayList<CategoryInfo> onLoadMore() {
			return null;
		}

	}
}
