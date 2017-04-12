package com.xu.googleplay.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.xu.googleplay.R;

/**
 * 自定义控件, 按照比例来决定布局高度
 *
 * 直接ImageView会有白边，裁剪缩放都不行，而且大小包裹内容的话也不行，要写死!
 * 这里就自定义一个控件，宽度就填充屏幕，高度不确定是根据当时的情况来动态设置
 * 动态设置，宽除以高，成一个比例，自定义了一个幀布局，让ImageView填充就可以了
 *
 * 幀布局在自定义或者动态填充页面的时候用途是非常广的
 *
 * 需要在valuse新建一个attrs文件，自定义属性，在这个类里要拿到2.43的属性值
 */
public class RatioLayout extends FrameLayout {

	private float ratio;

	public RatioLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//在这个类里要拿到2.43的属性值,在AttributeSet里拿
	public RatioLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取属性值
		// attrs.getAttributeFloatValue("", "ratio", -1);
		// 当自定义属性时, 系统会自动生成属性相关id, 此id通过R.styleable来引用

		//获取加载属性，参数2，要传int数组，在attrs里面拿，底层编译成R文件了。返回一对属性的集合
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.RatioLayout);
		// id = 属性名_具体属性字段名称 (此id系统自动生成)
		ratio = typedArray.getFloat(R.styleable.RatioLayout_ratio, -1);
		typedArray.recycle();// 回收typearray, 提高性能

		System.out.println("ratio:" + ratio);
	}

	public RatioLayout(Context context) {
		super(context);
	}

	/*测量控件的方法
	* 根据比率调整高度
	* 自定义控件的尺寸的调整，有3种核心的方法
	*
	* mare测量调整大小，layout布局设置位置，draw进行具体的位置，
	*
	* onMeasure在测量的时候调整位置，重写调整宽高
	* */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//参数传进来的不是宽和高，自定义控件传进来的宽高的模式

		// 1. 获取宽度
		// 2. 根据宽度和比例ratio, 计算控件的高度
		// 3. 重新测量控件
		
		//这个值转换成二进制是1000000000000000000000111001110
		//最前面的1表示模式，这个是宽高值111001110，在转成10进制（图片的像素）
		System.out.println("widthMeasureSpec:" + widthMeasureSpec);

		// MeasureSpec.AT_MOST; 至多模式, 控件有多大显示多大, wrap_content（左移30个0）
		// MeasureSpec.EXACTLY; 确定模式, 类似宽高写死成dip, match_parent
		// MeasureSpec.UNSPECIFIED; 未指定模式.动态的进行计算

		int width = MeasureSpec.getSize(widthMeasureSpec);// 获取宽度值
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);// 获取宽度模式
		int height = MeasureSpec.getSize(heightMeasureSpec);// 获取高度值
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);// 获取高度模式


		//（为了防止控件设置padding，不能拿空间的控件来类推，而是拿图片来类推）

		//因为根据宽，类推出高
		// 宽度确定,并且高度不确定, ratio合法, 才计算高度值
		if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && ratio > 0) {

			//因为现在在做封装，打造一款工具，为用户考虑
			//（为了防止控件设置padding，不能拿空间的控件来类推，而是拿图片来类推）
			// 图片宽度 = 控件宽度 - 左侧内边距 - 右侧内边距
			int imageWidth = width - getPaddingLeft() - getPaddingRight();

			// 图片高度 = 图片宽度/宽高比例
			int imageHeight = (int) (imageWidth / ratio + 0.5f);

			// 控件高度 = 图片高度 + 上侧内边距 + 下侧内边距
			height = imageHeight + getPaddingTop() + getPaddingBottom();

			// 根据最新的高度来重新生成heightMeasureSpec(高度模式是确定模式)
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
		}

		//最后在进行测量
		//默认就是super.onMeasure，在底层策略，自己去绘制的，暂时不用，所有放在最后
		// 按照最新的高度测量控件（宽度不用做修改，高度发生变化了，根据高度要生成heightMeasureSpec，因为要的是模式信息的heightMeasureSpec）
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
