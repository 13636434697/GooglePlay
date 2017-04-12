package com.xu.googleplay.ui.holder;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.BitmapUtils;
import com.xu.googleplay.R;
import com.xu.googleplay.http.HttpHelper;
import com.xu.googleplay.utils.BitmapHelper;
import com.xu.googleplay.utils.UIUtils;

/**
 * 首页轮播条holder
 *
 * 	BaseHolder还有其他作用，不一定用listView的adapter，所有的布局都要，加载布局，刷新数据。这里就封装了2个方法，
 * 	所以写一个头条新闻holder对象，在里面处理布局和数据，从而实现了头条新闻的细节模块和HomeFragment实现了解耦
 *
 * 	holder的数据是头条的数据，在picture里面。是arrayList里面塞了字符串
 *
 *	viewpager和indica不用布局文件，纯代码。因为jar包不能有xml的，所以为了以后用代码
 */
public class HomeHeaderHolder extends BaseHolder<ArrayList<String>> {

	private ViewPager mViewPager;

	//因为adapter要用data所以全局声明了一下，把传进来的data赋值一下
	private ArrayList<String> data;

	private LinearLayout llContainer;

	private int mPreviousPos;// 上个圆点位置

	//初始化布局
	@Override
	public View initView() {
		// 创建根布局, 相对布局
		RelativeLayout rlRoot = new RelativeLayout(UIUtils.getContext());
		//LayoutParams用布局参数来设置，找listView的父类
		// 初始化布局参数, 根布局上层控件是listview, 所以要使用listview定义的LayoutParams，指定宽和高
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(150));
		rlRoot.setLayoutParams(params);

		// ViewPager
		mViewPager = new ViewPager(UIUtils.getContext());
		RelativeLayout.LayoutParams vpParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		//这样做的好处，就是不用viewpager单独的设置参数了， mViewPager.setLayoutParams(vpParams);
		rlRoot.addView(mViewPager, vpParams);// 把viewpager添加给相对布局



		// 初始化指示器
		llContainer = new LinearLayout(UIUtils.getContext());
		llContainer.setOrientation(LinearLayout.HORIZONTAL);// 水平方向

		//线性布局要设置参数，用他父类参数来设置
		RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		// 设置内边距
		int padding = UIUtils.dip2px(10);
		llContainer.setPadding(padding, padding, padding, padding);

		// 添加规则, 设定展示位置
		llParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);// 底部对齐
		llParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);// 右对齐

		// 添加布局
		rlRoot.addView(llContainer, llParams);

		return rlRoot;
	}

	@Override
	public void refreshView(final ArrayList<String> data) {
		//因为adapter要用data所以全局声明了一下，把传进来的data赋值一下
		this.data = data;
		// 填充viewpager的数据
		mViewPager.setAdapter(new HomeHeaderAdapter());
		//要让viewpager循环滑动的话，数量要改成整数的最大值，把位置放在中间，因为一上来可能不能往回滑，还要保证是第一张图片
		mViewPager.setCurrentItem(data.size() * 10000);

		// 初始化指示器，这里是添加，拿到数据之后循环添加
		for (int i = 0; i < data.size(); i++) {
			ImageView point = new ImageView(UIUtils.getContext());

			//设置宽高都是包裹内容
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			if (i == 0) {// 第一个默认选中
				point.setImageResource(R.drawable.indicator_selected);
			} else {
				point.setImageResource(R.drawable.indicator_normal);

				params.leftMargin = UIUtils.dip2px(4);// 左边距
			}
			//把参数设置进去
			point.setLayoutParams(params);
			//添加了小点点
			llContainer.addView(point);
		}

		//监听viewpager的切换事件
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				//对position取余，因为这个position是整数的最大值
				position = position % data.size();

				// 当前点被选中，根据position可以定位到他的点
				ImageView point = (ImageView) llContainer.getChildAt(position);
				point.setImageResource(R.drawable.indicator_selected);

				// 上个点变为不选中
				ImageView prePoint = (ImageView) llContainer.getChildAt(mPreviousPos);
				prePoint.setImageResource(R.drawable.indicator_normal);

				//把上个点记下来
				mPreviousPos = position;

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		//实现viewpager自动滑动，自动轮播，用handler发消息，这里写了一个对象
		// UIUtils.getHandler().postDelayed(r, delayMillis)
		//启动轮播条自动播放
		HomeHeaderTask task = new HomeHeaderTask();
		task.start();
	}

	//实现viewpager自动滑动，自动轮播，用handler发消息，这里写了一个对象
	class HomeHeaderTask implements Runnable {

		//开始启动的方法
		public void start() {
			// 移除之前发送的所有消息, 避免消息重复，因为可能初始化多次
			UIUtils.getHandler().removeCallbacksAndMessages(null);
			//本来就是个runbable所以就this。延迟3秒发送消息，
			//也可以handlermessage，在handlermessage里面在处理消息，不想重写handlermessage。
			// 就扔出对象就是下面方法，然后在下面的run方面里面处理相关的消息
			UIUtils.getHandler().postDelayed(this, 3000);
		}

		//每隔3秒钟就走这个方法
		@Override
		public void run() {
			//这里就可以更新位置，拿到当前的位置
			int currentItem = mViewPager.getCurrentItem();
			currentItem++;
			//重新设置位置
			mViewPager.setCurrentItem(currentItem);

			// 继续发延时3秒消息, 实现内循环
			UIUtils.getHandler().postDelayed(this, 3000);
		}

	}

	// 填充viewpager的数据
	class HomeHeaderAdapter extends PagerAdapter {

		private BitmapUtils mBitmapUtils;

		public HomeHeaderAdapter() {
			mBitmapUtils = BitmapHelper.getBitmapUtils();
		}

		@Override
		public int getCount() {
			//要让viewpager循环滑动的话，数量要改成整数的最大值
			// return data.size();
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			//要让viewpager循环滑动的话，数量要改成整数的最大值，会导致这里的position变大，角标越界，所有这里要取余
			position = position % data.size();

			String url = data.get(position);

			ImageView view = new ImageView(UIUtils.getContext());
			//这里还要裁剪一下，要填充屏幕
			view.setScaleType(ScaleType.FIT_XY);
			mBitmapUtils.display(view, HttpHelper.URL + "image?name=" + url);

			//要把view添加到container
			container.addView(view);

			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

}
