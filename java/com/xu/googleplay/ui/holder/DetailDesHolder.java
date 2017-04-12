package com.xu.googleplay.ui.holder;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xu.googleplay.R;
import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.utils.UIUtils;

/**
 * 详情页-应用描述
 *
 */
public class DetailDesHolder extends BaseHolder<AppInfo> {

	private TextView tvDes;
	private TextView tvAuthor;
	private ImageView ivArrow;
	private RelativeLayout rlToggle;

	//初始化布局
	@Override
	public View initView() {
		//加载布局
		View view = UIUtils.inflate(R.layout.layout_detail_desinfo);

		tvDes = (TextView) view.findViewById(R.id.tv_detail_des);
		tvAuthor = (TextView) view.findViewById(R.id.tv_detail_author);
		ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
		rlToggle = (RelativeLayout) view.findViewById(R.id.rl_detail_toggle);

		//设置下拉的点击事件
		rlToggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggle();
			}
		});

		return view;
	}

	//刷新界面
	@Override
	public void refreshView(AppInfo data) {
		//设置描述
		tvDes.setText(data.des);
		//设置作者
		tvAuthor.setText(data.author);

		//方法：post在代码没有问题，就是执行顺序的问题。
		// 放在消息队列中运行, 解决当只有三行描述时也是7行高度的bug
		tvDes.post(new Runnable() {

			@Override
			public void run() {
				// 刷新界面一上来就是7行高度，默认展示7行的高度
				int shortHeight = getShortHeight();
				//拿到目前的参数，他的父类是一个LinearLayout
				mParams = (LinearLayout.LayoutParams) tvDes.getLayoutParams();
				//修改高度
				mParams.height = shortHeight;
				//设置参数
				tvDes.setLayoutParams(mParams);
			}
		});
	}

	//默认开关是没有打开的
	private boolean isOpen = false;
	private LinearLayout.LayoutParams mParams;

	protected void toggle() {
		//拿到比较短的高度
		int shortHeight = getShortHeight();
		//拿到比较长的高度
		int longHeight = getLongHeight();

		//设置初始值
		ValueAnimator animator = null;
		if (isOpen) {
			// 如果打开的就关闭
			isOpen = false;
			//完整展示的高度超过7行
			if (longHeight > shortHeight) {// 只有描述信息大于7行,才启动动画
				//关闭，就是长变短
				animator = ValueAnimator.ofInt(longHeight, shortHeight);
			}
		} else {
			// 如果关闭的就打开
			isOpen = true;
			if (longHeight > shortHeight) {// 只有描述信息大于7行,才启动动画
				//打开，就是短变长
				animator = ValueAnimator.ofInt(shortHeight, longHeight);
			}
		}

		//animator可能没有初始化，需要判断下
		if (animator != null) {// 只有描述信息大于7行,才启动动画
			//给动画设置监听
			animator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator arg0) {
					//获取高度
					Integer height = (Integer) arg0.getAnimatedValue();
					//修改布局参数
					mParams.height = height;
					//设置布局参数
					tvDes.setLayoutParams(mParams);
				}

			});

			//属性动画的监听事件，
			animator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {

				}
				//动画结束，才自动滑动屏幕
				@Override
				public void onAnimationEnd(Animator arg0) {
					// ScrollView要滑动到最底部,拿到scrollView对象
					final ScrollView scrollView = getScrollView();

					//hanlder机制：把消息发给消息队列，然后loop，在循环消息队列，拿到消息队列就执行，
					// runnable不是线程，Thread。start起来之后，thread是一个线程，runnable是线程里运行的对象
					// 不一定在子线程里运行，光runnable也可以跑，把runnable也发到消息队列，让loop也去循环，run方法在主线程，因为loop就在主线程

					// 为了运行更加安全和稳定, 可以讲滑动到底部方法放在消息队列中执行
					//不用hanlder，view对象也可以post
					scrollView.post(new Runnable() {

						@Override
						public void run() {
							scrollView.fullScroll(ScrollView.FOCUS_DOWN);// 滚动到底部
						}
					});

					//展开后更新小箭头
					if (isOpen) {
						ivArrow.setImageResource(R.drawable.arrow_up);
					} else {
						ivArrow.setImageResource(R.drawable.arrow_down);
					}

				}

				@Override
				public void onAnimationCancel(Animator arg0) {

				}
			});

			//设置动画时间
			animator.setDuration(200);
			//开启动画
			animator.start();
		}
	}

	/**
	 * 获取7行textview的高度
	 */
	private int getShortHeight() {
		// 模拟一个textview,设置最大行数为7行, 计算该虚拟textview的高度, 从而知道tvDes在展示7行时应该多高
		int width = tvDes.getMeasuredWidth();// 原生的宽度

		TextView view = new TextView(UIUtils.getContext());
		view.setText(getData().des);// 设置文字
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);// 文字大小一致
		view.setMaxLines(7);// 最大行数为7行


		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);// 宽不变, 确定值, match_parent
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(2000,MeasureSpec.AT_MOST);// 高度包裹内容, wrap_content;当包裹内容时,
										// 参1表示尺寸最大值,暂写2000, 也可以是屏幕高度

		//对上面的TextView进行测量，不能传0和0，需要知道确切的宽高
		view.measure(widthMeasureSpec, heightMeasureSpec);
		// 返回测量后的高度
		return view.getMeasuredHeight();
	}

	/**
	 * 获取完整textview的高度
	 */
	private int getLongHeight() {
		// 模拟一个textview,设置最大行数为7行, 计算该虚拟textview的高度, 从而知道tvDes在展示7行时应该多高
		int width = tvDes.getMeasuredWidth();// 宽度

		TextView view = new TextView(UIUtils.getContext());
		view.setText(getData().des);// 设置文字
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);// 文字大小一致
		//和上面方法一样，就注掉这一行代码
		// view.setMaxLines(7);// 最大行数为7行

		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);// 宽不变, 确定值, match_parent
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(2000,MeasureSpec.AT_MOST);// 高度包裹内容, wrap_content;当包裹内容时,
										// 参1表示尺寸最大值,暂写2000, 也可以是屏幕高度

		// 开始测量
		view.measure(widthMeasureSpec, heightMeasureSpec);
		return view.getMeasuredHeight();// 返回测量后的高度
	}

	//目的，就是当展开详情的时候，屏幕自动往下拉，需要用到ScrollView
	// 获取ScrollView, 一层一层往上找,
	// 知道找到ScrollView后才返回;注意:一定要保证父控件或祖宗控件有ScrollView,否则死循环
	private ScrollView getScrollView() {
		//先找到第一个父类
		ViewParent parent = tvDes.getParent();

		//循环寻找，如果不是ScrollView，继续循环
		while (!(parent instanceof ScrollView)) {
			parent = parent.getParent();
		}

		//只要循环跳出来，就返回ScrollView，只要能出来就一定是ScrollView
		return (ScrollView) parent;
	}

}
