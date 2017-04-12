package com.xu.googleplay.ui.holder;

import java.util.ArrayList;


import android.animation.ValueAnimator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.xu.googleplay.R;
import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.http.HttpHelper;
import com.xu.googleplay.utils.BitmapHelper;
import com.xu.googleplay.utils.UIUtils;
import com.xu.googleplay.domain.AppInfo.SafeInfo;
import android.animation.Animator.AnimatorListener;
import android.animation.Animator;



/**
 * 应用详情页-安全模块
 *
 * 写布局文件的时候，图片和文字数量不确定，应该考虑最多时候应该多少，该显示的显示，该隐藏的时候隐藏
 */
public class DetailSafeHolder extends BaseHolder<AppInfo> {
	//数量太多，成数组
	private ImageView[] mSafeIcons;// 安全标识图片
	private ImageView[] mDesIcons;// 安全描述图片
	private TextView[] mSafeDes;// 安全描述文字
	private LinearLayout[] mSafeDesBar;// 安全描述条目(图片+文字)
	private BitmapUtils mBitmapUtils;

	private RelativeLayout rlDesRoot;
	private LinearLayout llDesRoot;
	private ImageView ivArrow;

	private int mDesHeight;
	private LinearLayout.LayoutParams mParams;

	@Override
	public View initView() {
		View view = UIUtils.inflate(R.layout.layout_detail_safeinfo);

		//4个ImageView
		mSafeIcons = new ImageView[4];
		mSafeIcons[0] = (ImageView) view.findViewById(R.id.iv_safe1);
		mSafeIcons[1] = (ImageView) view.findViewById(R.id.iv_safe2);
		mSafeIcons[2] = (ImageView) view.findViewById(R.id.iv_safe3);
		mSafeIcons[3] = (ImageView) view.findViewById(R.id.iv_safe4);

		mDesIcons = new ImageView[4];
		mDesIcons[0] = (ImageView) view.findViewById(R.id.iv_des1);
		mDesIcons[1] = (ImageView) view.findViewById(R.id.iv_des2);
		mDesIcons[2] = (ImageView) view.findViewById(R.id.iv_des3);
		mDesIcons[3] = (ImageView) view.findViewById(R.id.iv_des4);

		mSafeDes = new TextView[4];
		mSafeDes[0] = (TextView) view.findViewById(R.id.tv_des1);
		mSafeDes[1] = (TextView) view.findViewById(R.id.tv_des2);
		mSafeDes[2] = (TextView) view.findViewById(R.id.tv_des3);
		mSafeDes[3] = (TextView) view.findViewById(R.id.tv_des4);

		mSafeDesBar = new LinearLayout[4];
		mSafeDesBar[0] = (LinearLayout) view.findViewById(R.id.ll_des1);
		mSafeDesBar[1] = (LinearLayout) view.findViewById(R.id.ll_des2);
		mSafeDesBar[2] = (LinearLayout) view.findViewById(R.id.ll_des3);
		mSafeDesBar[3] = (LinearLayout) view.findViewById(R.id.ll_des4);

		rlDesRoot = (RelativeLayout) view.findViewById(R.id.rl_des_root);
		//添加点击事件
		rlDesRoot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 打开或者关闭安全描述信息
				toggle();
			}
		});

		mBitmapUtils = BitmapHelper.getBitmapUtils();

		llDesRoot = (LinearLayout) view.findViewById(R.id.ll_des_root);
		ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);

		return view;
	}

	private boolean isOpen = false;// 标记安全描述开关状态,默认关

	// 打开或者关闭安全描述信息
	//这里要用到属性动画，高度可以发生不断的变化
	// 导入jar包: nineoldandroids-2.4.0.jar
	protected void toggle() {
		//这里要用到属性动画，高度可以发生不断的变化

		//界面刷新完之后才能决定哪些隐藏还是显示，界面确定下来之后才能拿到高度
		ValueAnimator animator = null;
		//如果isOpen是开的状态
		if (isOpen) {
			// 关闭
			isOpen = false;
			// 属性动画，拿到属性动画的高度
			animator = ValueAnimator.ofInt(mDesHeight, 0);// 从某个值变化到某个值
		} else {
			// 开启
			isOpen = true;
			// 属性动画，拿到属性动画的高度
			animator = ValueAnimator.ofInt(0, mDesHeight);
		}

		// 动画更新的监听
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			// 启动动画之后, 会不断回调此方法来获取最新的值
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				// 获取最新的高度值
				Integer height = (Integer) animator.getAnimatedValue();

				System.out.println("最新高度:" + height);

				// 重新修改布局高度，拿到的新的高度
				mParams.height = height;
				//设置布局的参数
				llDesRoot.setLayoutParams(mParams);
			}
		});

		//监听的动画结束事件
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// 动画结束的事件
				// 更新改变小箭头的方向
				//如果是打开的状态
				if (isOpen) {
					//箭头应该向上
					ivArrow.setImageResource(R.drawable.arrow_up);
				} else {
					//箭头应该向下
					ivArrow.setImageResource(R.drawable.arrow_down);
				}
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});

		//设置动画的时间
		animator.setDuration(200);// 动画时间
		animator.start();// 启动动画
	}

	//加载数据
	@Override
	public void refreshView(AppInfo data) {
		//数据是一个集合
		ArrayList<AppInfo.SafeInfo> safe = data.safe;

		//遍历这个集合，永远遍历4次，因为总共就4个imageView
		for (int i = 0; i < 4; i++) {
			//判断如果i大于下标就越界了
			if (i < safe.size()) {
				//这里还没有越界，走在这里的话，前面图片都要显示出来
				// 安全标识图片，拿到对应的对象
				//这里是目前要显示的图片
				SafeInfo safeInfo = safe.get(i);
				//这里是imageView对象，在数组里面，第i个
				mBitmapUtils.display(mSafeIcons[i], HttpHelper.URL + "image?name=" + safeInfo.safeUrl);
				// 安全描述文字
				mSafeDes[i].setText(safeInfo.safeDes);
				// 安全描述图片
				mBitmapUtils.display(mDesIcons[i], HttpHelper.URL + "image?name=" + safeInfo.safeDesUrl);
			} else {
				//下标越界之后
				// 剩下不应该显示的图片
				//如果到这里是超过边界了，这些图片应该隐藏掉，取出第i个图片隐藏掉
				mSafeIcons[i].setVisibility(View.GONE);

				// 隐藏多余的描述条目
				mSafeDesBar[i].setVisibility(View.GONE);
			}
		}

		//界面刷新完之后才能决定哪些隐藏还是显示，界面确定下来之后才能拿到高度
		// 获取安全描述的完整高度
		//首先是测量一下，不需要测量，让底层自己测量
		llDesRoot.measure(0, 0);
		//这是测量后的高度值
		mDesHeight = llDesRoot.getMeasuredHeight();

		System.out.println("安全描述高度:" + mDesHeight);

		// 修改安全描述布局高度为0,达到隐藏效果
		//目前参数要先拿到
		mParams = (LinearLayout.LayoutParams) llDesRoot.getLayoutParams();
		//只修改高度
		mParams.height = 0;
		//设置布局参数
		llDesRoot.setLayoutParams(mParams);
	}

}
