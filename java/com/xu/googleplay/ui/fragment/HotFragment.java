package com.xu.googleplay.ui.fragment;


import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xu.googleplay.ui.holder.HotProtocol;
import com.xu.googleplay.ui.view.FlowLayout;
import com.xu.googleplay.ui.view.LoadingPage.ResultState;
import com.xu.googleplay.ui.view.MyFlowLayout;
import com.xu.googleplay.utils.DrawableUtils;
import com.xu.googleplay.utils.UIUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * 排行
 *
 * 需要用到自定义控件FlowLayout
 * 这个自定义控件的特点是可以按照当前布局的宽高一行一行排列，会安排的很整齐
 */
public class HotFragment extends BaseFragment {

	private ArrayList<String> data;

    //只有成功才走此方法，实现了基类的方法
	@Override
	public View onCreateSuccessView() {
		// 支持上下滑动，因为用了ScrollView
		ScrollView scrollView = new ScrollView(UIUtils.getContext());
		//自定义控件FlowLayout
		//FlowLayout flow = new FlowLayout(UIUtils.getContext());
		MyFlowLayout flow = new MyFlowLayout(UIUtils.getContext());

		int padding = UIUtils.dip2px(10);
		flow.setPadding(padding, padding, padding, padding);// 设置内边距

		//字和字之间设置距离，单位是dp
		//flow.setHorizontalSpacing(UIUtils.dip2px(6));// 水平间距
		//flow.setVerticalSpacing(UIUtils.dip2px(8));// 竖直间距

		//FlowLayout用textView填充满
		for (int i = 0; i < data.size(); i++) {
			//这是数据的文字
			final String keyword = data.get(i);
			TextView view = new TextView(UIUtils.getContext());
			//设置文字
			view.setText(keyword);

			//给TextView设置背景，颜色随机，背景还是圆角矩形，（图片不能做，xml要定义颜色也不行）
			//代码方法，new一个圆角矩形，然后在给圆角矩形设置动态的颜色DrawableUtils
			//设置文字的属性
			view.setTextColor(Color.WHITE);
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);// 18sp
			view.setPadding(padding, padding, padding, padding);
			view.setGravity(Gravity.CENTER);

			// 生成随机颜色
			Random random = new Random();
			int r = 30 + random.nextInt(200);
			int g = 30 + random.nextInt(200);
			int b = 30 + random.nextInt(200);

			int color = 0xffcecece;// 按下后偏白的背景色

			//拿到背景对象，传颜色和半径，颜色随机，还有变色，是一个状态选择器
			// GradientDrawable bgNormal = DrawableUtils.getGradientDrawable(Color.rgb(r, g, b), UIUtils.dip2px(6));默认图片
			// GradientDrawable bgPress = DrawableUtils.getGradientDrawable(color, UIUtils.dip2px(6));按下图片
			// StateListDrawable selector = DrawableUtils.getSelector(bgNormal,bgPress);生成状态选择器

			//上面三部太烦了，需要重载封装下，这里就调用一个方法就可以了
			StateListDrawable selector = DrawableUtils.getSelector(Color.rgb(r, g, b), color, UIUtils.dip2px(6));
			//给textView设置背景
			view.setBackgroundDrawable(selector);
			//把textView的文字添加给FlowLayout
			flow.addView(view);

			// 只有设置点击事件, 状态选择器才起作用
			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(UIUtils.getContext(), keyword,Toast.LENGTH_SHORT).show();
				}
			});
		}
		//ScrollView添加视图
		scrollView.addView(flow);
		//ScrollView是根布局，返回出去
		return scrollView;
	}

	//加载网络数据
	@Override
	public ResultState onLoad() {
		HotProtocol protocol = new HotProtocol();
		data = protocol.getData(0);
		return check(data);
	}

}
