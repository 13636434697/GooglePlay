package com.xu.googleplay.ui.holder;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.lidroid.xutils.BitmapUtils;
import com.xu.googleplay.R;
import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.http.HttpHelper;
import com.xu.googleplay.utils.BitmapHelper;
import com.xu.googleplay.utils.UIUtils;

/**
 * 首页详情页-截图
 *
 */
public class DetailPicsHolder extends BaseHolder<AppInfo> {

	//在view对象里可以findViewById，拿到ImageView，这里就定义一个数组，
	// 不要在成员变量里new，可能空指针，因为代码执行顺序的愿意，因为initView方法执行比变量早，继承了baseholder。
	private ImageView[] ivPics;
	private BitmapUtils mBitmapUtils;

	//初始化布局
	@Override
	public View initView() {
		//加载xml布局
		View view = UIUtils.inflate(R.layout.layout_detail_picinfo);

		ivPics = new ImageView[5];
		ivPics[0] = (ImageView) view.findViewById(R.id.iv_pic1);
		ivPics[1] = (ImageView) view.findViewById(R.id.iv_pic2);
		ivPics[2] = (ImageView) view.findViewById(R.id.iv_pic3);
		ivPics[3] = (ImageView) view.findViewById(R.id.iv_pic4);
		ivPics[4] = (ImageView) view.findViewById(R.id.iv_pic5);

		mBitmapUtils = BitmapHelper.getBitmapUtils();

		return view;
	}

	//刷新数据
	@Override
	public void refreshView(AppInfo data) {
		//找data.screen，保存的是一张一张截图的网络链接
		final ArrayList<String> screen = data.screen;
		//遍历上面的集合
		for (int i = 0; i < 5; i++) {
			//小于的话，数还够
			if (i < screen.size()) {
				//这里要加载图片
				mBitmapUtils.display(ivPics[i], HttpHelper.URL + "image?name=" + screen.get(i));

				//给图片设置点击事件，就是一个大的viewPager，问题就是跳页面的时候需要知道链接，
				// 通过intent来传递参数data.screen，用bitmap重写加载图片。ivPics[i]可以传当前默认的点击的图片
//				ivPics[i].setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						//跳转activity, activity展示viewpager
//						//将集合通过intent传递过去, 当前点击的位置i也可以传过去
//						Intent intent = new Intent();
//						//集合直接塞进去
//						intent.putExtra("list", screen);
//						//然后startAcivity就可以了
//					}
//				});
			} else {
				//剩下的ImageView多出来就隐藏掉
				ivPics[i].setVisibility(View.GONE);
			}
		}

	}

}
