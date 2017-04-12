package com.xu.googleplay.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.xu.googleplay.utils.UIUtils;

/*
* 自定义控件
*
* 像流一样的控件，实现构造方法
*
* */
public class MyFlowLayout extends ViewGroup {
	//有了宽度之后，一行有很多控件，怎么知道控件的宽度还有多少。给全局维护了，目前使用了多少宽度
	private int mUsedWidth;// 当前行已使用的宽度

	//整体宽度还要考虑到控件之间的水平间距
	private int mHorizontalSpacing = UIUtils.dip2px(6);// 水平间距
	private int mVerticalSpacing = UIUtils.dip2px(8);// 竖直间距

	//初始化对象，然后添加一行行的子控件
	private Line mLine;// 当前行对象

	//行对象越来越多，需要集合来维护
	private ArrayList<Line> mLineList = new ArrayList<Line>();// 维护所有行的集合

	//添加完之后，还要判断是不是最大的行数（因为行数是不能一直添加下去的）
	private static final int MAX_LINE = 100;// 最大行数是100行

	public MyFlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyFlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyFlowLayout(Context context) {
		super(context);
	}

	//onMeasure完成之后，只是测量，然后放在了行对象里面，没有处理具体的位置的展现，这里处理位置
	//layout把一行画好了，这里需要把整个控件里，一行一行在画好，
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//padding值也要算进来，传进来的时候需要重写赋值
		int left = l + getPaddingLeft();
		int top = t + getPaddingTop();

		// 遍历line里面的所有行对象, 设置每行位置
		for (int i = 0; i < mLineList.size(); i++) {
			Line line = mLineList.get(i);
			//有了line这个方法之后，就调用里面的layout设置参数
			//padding值也要算进来，传进来的时候需要重写赋值
			line.layout(left, top);
			//第一行可以这样，每画一行top值会越来越大所以要累加起来
			top += line.mMaxHeight + mVerticalSpacing;// 更新top值。加上竖起来的间距
		}
	}

	//重点重写尺寸测量的方法，把整个控件的高度和宽度
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 首先获取整体的控件的宽度，要考虑控件可能有padding，要减去padding（有效宽度）
		int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()- getPaddingRight();
		// 首先获取整体的控件的高度，要考虑控件可能有padding，要减去padding（有效高度）
		int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

		// 获取宽高模式
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		//遍历viewGroup所有的子类，看子类的宽高，从而决定什么时候换行
		int childCount = getChildCount();// 获取所有子控件数量
		for (int i = 0; i < childCount; i++) {
			//拿到当前的子控件
			View childView = getChildAt(i);


			// 如果父控件是确定模式, 子控件包裹内容;否则子控件模式和父控件一致
			//宽度和模式，宽度是有效宽度。如果父类的模式确定（MeasureSpec.EXACTLY）的话，就包裹内容，否者和父类模式是一样的
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,(widthMode == MeasureSpec.EXACTLY) ? MeasureSpec.AT_MOST : widthMode);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,(heightMode == MeasureSpec.EXACTLY) ? MeasureSpec.AT_MOST : heightMode);

			//开始测量子控件，因为子控件是new的，没有用到布局文件，参数要搞出来，就在上面搞出来了
			childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

			// 如果当前行对象为空, 初始化一个行对象
			//初始化对象，然后添加一行行的子控件
			if (mLine == null) {
				mLine = new Line();
			}

			// 获取子控件测量的宽度
			int childWidth = childView.getMeasuredWidth();

			//有了宽度之后，一行有很多控件，怎么知道控件的宽度还有多少。给全局维护了，目前使用了多少宽度
			mUsedWidth += childWidth;// 已使用宽度增加一个子控件宽度

			//上面的宽度还要判断是不是超过了边界
			if (mUsedWidth < width) {//判断当前宽度，是否大于整体的有效宽度
				//没有超出边界
				//给行对象添加子控件
				mLine.addView(childView);// 更当前行对象添加子控件

				//还要考虑到水平的间距
				mUsedWidth += mHorizontalSpacing;// 增加一个水平间距

				//加上了水平边距之后还要判断，是不是超出整体宽度
				if (mUsedWidth > width) {
					// 增加水平间距之后, 就超出了边界, 此时需要换行
					//要换行的话，要有行，行就要new新的对象，要有一个行的对象
					//如果newLine这个方法失败的话
					if (!newLine()) {
						break;// 如果创建行失败,就结束循环,不再添加
					}
				}

			} else {
				// 已超出边界
				//有2种情况，那2种情况的处理方式都是不一样的

				// 1.当前没有任何控件, 一旦添加当前子控件, 就超出边界(子控件很长)
				//获取mLine里面看子类的数量，
				if (mLine.getChildCount() == 0) {
					mLine.addView(childView);// 强制添加到当前行

					//添加完之后在换的行，宽度已经是0了
					// 换行
					if (!newLine()) {
						break;
					}
				} else {

					// 2.当前有控件, 一旦添加, 超出边界
					if (!newLine()) {// 先换行
						break;
					}
					//先换行在添加的，所有宽度是0，所有要更新宽度
					//添加子类
					mLine.addView(childView);
					// 更新已使用宽度
					mUsedWidth += childWidth + mHorizontalSpacing;
				}
			}

		}

		//for循环一个一个添加，不断的换行，已经初始化好了
		// 最后，排列好了，保存最后一行的行对象
		//有时候遍历到最后一行，没有添加到集合里面，永远保存的上一条数据
		//如果行对象不等于空，并且，行对象里面有子类的，并且，集合里面不包含行对象
		if (mLine != null && mLine.getChildCount() != 0 && !mLineList.contains(mLine)) {
			//添加行对象
			mLineList.add(mLine);
		}
		//保存行对象之后，还需要测量，不用管pading值，因为pading值也要算到里面来
		int totalWidth = MeasureSpec.getSize(widthMeasureSpec);// 控件整体宽度


		//控件的整体高度，取决于多少多少行，每一行有多高。
		int totalHeight = 0;// 控件整体高度
		//因为每行都在集合里面，所以遍历集合
		for (int i = 0; i < mLineList.size(); i++) {
			//拿到当前行
			Line line = mLineList.get(i);
			//拿出最高的高度，就是整行的高度
			totalHeight += line.mMaxHeight;
		}

		//
		totalHeight += (mLineList.size() - 1) * mVerticalSpacing;// 增加竖直间距
		totalHeight += getPaddingTop() + getPaddingBottom();// 增加上下边距

		// 根据最新的宽高来测量整体布局的大小
		//这里直接调用，不拼模式等等，直接传尺寸值
		setMeasuredDimension(totalWidth, totalHeight);
		//onMeasure底层调用了setMeasuredDimension的方法，设置他的尺寸，传的尺寸值
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	// 换行的方法
	private boolean newLine() {
		//行对象越来越多，需要添加到集合，然后在new下一行
		mLineList.add(mLine);// 保存上一行数据

		//添加完之后，还要判断是不是最大的行数（因为行数是不能一直添加下去的）
		if (mLineList.size() < MAX_LINE) {
			// 可以继续添加，还是那个引用，但是已经指向第二行
			mLine = new Line();
			//这里是添加的是新的一行，所以已使用宽度清零
			mUsedWidth = 0;
			// 创建行成功
			return true;
		}

		return false;// 创建行失败
	}

	// 每一行的对象封装，在对每一行的对象排列好
	class Line {

		private int mTotalWidth;// 维护这行所有子控件总体占用的宽度，当前所有控件总宽度
		public int mMaxHeight;// 当前控件的高度(以最高的控件为准)

		//因为里面都是子控件，所以需要自合来维护
		private ArrayList<View> mChildViewList = new ArrayList<View>();// 当前行所有子控件集合

		//每行应该有添加子类的方法
		// 添加一个子控件
		public void addView(View view) {
			//就给集合添加子控件
			mChildViewList.add(view);
			// 拿到了总宽度增加，维护这行所有子控件总体占用的宽度
			mTotalWidth += view.getMeasuredWidth();

			//当前行最高的高度为准
			//先拿到高度值
			int height = view.getMeasuredHeight();
			// 0 10 20 10
			//拿到的高度和总高度比对，
			//总高度是否小于当前最高度，小于的话高度值就等于最高的值，否者还是自身的高度
			mMaxHeight = mMaxHeight < height ? height : mMaxHeight;
		}

		//获取mLine里面看子类的数量，
		public int getChildCount() {
			return mChildViewList.size();
		}

		// 每一行的对象封装，在对每一行的对象排列好，子控件位置设置
		// 首先要知道布局的起始位置，左上角开始left和top的值传进去
		public void layout(int left, int top) {
			//调用一次就可以了，不用每次都调用
			int childCount = getChildCount();

			//情况：子控件宽高，还没有完全确定下来，
			// 子控件画不够的话，换行，但是上一行会空出来，需要把剩余的距离要平均分配给其他子控件
			// 将剩余空间分配给每个子控件
			//整个控件的宽度，减去左右间距
			int validWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();// 屏幕总有效宽度
			// 计算剩余宽度，有效宽度减去剩余宽度，在减去水平总间距，（子控件乘以水平的间距）
			int surplusWidth = validWidth - mTotalWidth - (childCount - 1) * mHorizontalSpacing;

			//如果有剩余的宽度
			if (surplusWidth >= 0) {
				// 有剩余空间
				//剩余的控件除以拿到子控件的个数（搞的更精确，可以float，在转成int，加上0.5f，四舍五入）
				//调用一次就可以了，不用每次都调用
				int space = (int) ((float) surplusWidth / childCount + 0.5f);// 平均每个控件分配的大小


				// 上面拿到宽度之后，可以重新测量子控件
				//通过遍历拿到子控件
				for (int i = 0; i < childCount; i++) {
					//集合里面拿取子控件
					View childView = mChildViewList.get(i);

					//测量之后的宽高
					int measuredWidth = childView.getMeasuredWidth();
					int measuredHeight = childView.getMeasuredHeight();

					//把剩余的空间，平均加到每一个控件里
					measuredWidth += space;// 宽度增加

					//宽度和模式（宽度已经确定了）
					int widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY);
					//高度和模式（高度已经确定了）
					int heightMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);

					// 重新测量控件
					childView.measure(widthMeasureSpec, heightMeasureSpec);

					//在控件设定位置之前，考虑一个问题，高度可能不一样，
					//拿到最高控件的高度减去当前的高度在除以二，就是需要下移的高度
					// 当控件比较矮时,需要居中展示, 竖直方向需要向下有一定偏移
					int topOffset = (mMaxHeight - measuredHeight) / 2;

					if (topOffset < 0) {
						topOffset = 0;
					}

					//下面就开始设置控件的位置了（左上和右下）
					//left和top值，但是top值要偏移，所以加上topOffset。right值就是左边的值+控件的宽。bottom就是高度+偏移值+控件的高
					childView.layout(left, top + topOffset, left + measuredWidth, top + topOffset + measuredHeight);
					//还有一种情况，第一个和第二个的值是不一样的，比原来多了第一个控件和间距
					//更新left值，当前的宽度加上水平间距，其他都不用改
					left += measuredWidth + mHorizontalSpacing;
				}

			} else {
				//surplusWidth可能是负值，因为控件比屏幕还长
				// 这个控件很长, 占满整行，
				View childView = mChildViewList.get(0);//拿到当前的控件
				//设置位置，不同偏移了右边还要加上测量的宽度，下边要加上测量的高度
				childView.layout(left, top,left + childView.getMeasuredWidth(),top + childView.getMeasuredHeight());
			}

		}

	}

}
