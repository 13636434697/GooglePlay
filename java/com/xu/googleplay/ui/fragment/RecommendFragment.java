package com.xu.googleplay.ui.fragment;


import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xu.googleplay.http.protocol.RecommendProtocol;
import com.xu.googleplay.ui.view.LoadingPage.ResultState;
import com.xu.googleplay.ui.view.fly.ShakeListener;
import com.xu.googleplay.ui.view.fly.StellarMap;
import com.xu.googleplay.utils.UIUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * 推荐标签
 * 用到自定义控件，在网上找的，fly文件夹，需要了解下
 */
public class RecommendFragment extends BaseFragment {

	private ArrayList<String> data;
    //只有成功才走此方法，实现了基类的方法
	//下面加载数据成功之后到这个方法
	@Override
	public View onCreateSuccessView() {
		//它里面要填充数据控件里面要填充文字
		final StellarMap stellar = new StellarMap(UIUtils.getContext());
		//有点像listView，但是StellarMap他自己的apapter，所以写apapter要照着人家写
		stellar.setAdapter(new RecommendAdapter());
		// 指定随机方式, 将控件划分为9行6列的的格子, 然后在格子中随机展示
		stellar.setRegularity(6, 9);

		// 屏幕适配更好，所以先转换好，10dp
		int padding = UIUtils.dip2px(10);
		// 设置内边距10dp，因为有时候字和边距靠的太近
		stellar.setInnerPadding(padding, padding, padding, padding);

		//创建页面的时候就要显示出来，不然必须要手滑动之后才会显示
		// 设置默认页面, 第一组数据
		stellar.setGroup(0, true);

		//摇晃的效果
		ShakeListener shake = new ShakeListener(UIUtils.getContext());
		//这是一个回调的方法，如果有摇晃就会跑到这个方法里来
		shake.setOnShakeListener(new ShakeListener.OnShakeListener() {

			@Override
			public void onShake() {
				stellar.zoomIn();// 跳到下一页数据
			}
		});

		return stellar;
	}

	//在加载数据的时候
	@Override
	public ResultState onLoad() {
		RecommendProtocol protocol = new RecommendProtocol();
		data = protocol.getData(0);
		return check(data);
	}

	//有点像listView，但是StellarMap他自己的apapter，所以写apapter要照着人家写
	class RecommendAdapter implements StellarMap.Adapter {

		// 返回组的个数
		@Override
		public int getGroupCount() {
			//有几十个数据，分为2组
			return 2;
		}

		// 返回某组的item个数
		// group是几组的多少个
		@Override
		public int getCount(int group) {
			//每一组的数量，等于我的总数除以组数
			int count = data.size() / getGroupCount();
			//判断是不是最后一页
			if (group == getGroupCount() - 1) {
				// 可能除不净的话，把余下来的数量追加在最后一页, 保证数据完整不丢失
				//总数量取余组数，把他加在每组的数量上
				count += data.size() % getGroupCount();
			}

			return count;
		}

		// 初始化布局
		@Override
		public View getView(int group, int position, View convertView) {
			// 因为position每组都会从0开始计数, 所以需要将前面几组数据的个数加起来,才能确定当前组获取数据的角标位置
			//当前组的数量group和上一组的数量getCount(group - 1)追加在一起，才是当前的位置
			position += (group) * getCount(group - 1);


			//上面有个data，就拿到数据了
			// System.out.println("pos:" + position);
			final String keyword = data.get(position);
			//因为知道是TextView，所以就直接new了，还有一个convertView，数量少就不重用了
			TextView view = new TextView(UIUtils.getContext());
			view.setText(keyword);

			Random random = new Random();
			// 随机大小, 16-25
			int size = 16 + random.nextInt(10);
			//还可以带单位，其实是一个枚举
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

			// 随机颜色
			// r g b, 0-255 -> 30-230, 颜色值不能太小或太大, 从而避免整体颜色过亮或者过暗
			int r = 30 + random.nextInt(200);
			int g = 30 + random.nextInt(200);
			int b = 30 + random.nextInt(200);
			//Color这个类，能够根据三原色生成一个颜色
			view.setTextColor(Color.rgb(r, g, b));
			//给具体的关键字做点击事件
			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(UIUtils.getContext(), keyword,Toast.LENGTH_SHORT).show();
				}
			});

			return view;
		}

		// 返回下一组的id，因为返回的下一组的号码都不一样
		//下一页数据是第几组
		@Override
		public int getNextGroupOnZoom(int group, boolean isZoomIn) {
			// 返回下一组的id，因为返回的下一组的号码都不一样
			// isZoomIn往下滑是true，网上滑是false
			System.out.println("isZoomIn:" + isZoomIn);
			if (isZoomIn) {
				// 往下滑加载上一页
				if (group > 0) {
					//回传回来当前组group
					group--;
				} else {
					// 跳到最后一页
					// 已经是第一页了，就只能跳到最后一页，做成循环
					group = getGroupCount() - 1;
				}
			} else {
				// 往上滑加载下一页
				//小于最后一页的话
				if (group < getGroupCount() - 1) {
					group++;
				} else {
					// 跳到第一页
					group = 0;
				}
			}
			return group;
		}

	}

}
