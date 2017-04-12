package com.xu.googleplay.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.http.protocol.HomeProtocol;
import com.xu.googleplay.ui.activity.HomeDetailActivity;
import com.xu.googleplay.ui.adapter.MyBaseAdapter;
import com.xu.googleplay.ui.holder.BaseHolder;
import com.xu.googleplay.ui.holder.HomeHeaderHolder;
import com.xu.googleplay.ui.holder.HomeHolder;
import com.xu.googleplay.ui.view.MyListView;
import com.xu.googleplay.utils.UIUtils;
import com.xu.googleplay.ui.view.LoadingPage.ResultState;

import java.util.ArrayList;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {
    //准备一个集合来测试用
    private ArrayList<AppInfo> data;


    //一定要刷新view，所有要调用这个方法，底层把数据塞进去
    // 设置轮播条数据
    //这个data不对，是appinfo，这里要的是一堆字符串集合，在HomeProtocol里面解析
    //这里暴漏一个方法，让其他方法能够获取到这个数据
    //把这个搞成全局的了
    // 轮播条数据
    private ArrayList<String> mPictureList;

    //只有成功才走此方法，实现了基类的方法
    // 如果加载数据成功, 就回调此方法, 在主线程运行
    //进来先走onLoad这个方法，成功后，在走这个方法
    @Override
    public View onCreateSuccessView() {
        //给页面设置个列表页,穿的上下文是自己工具类里面的上下文
        //使用的是自己的listView，这样可以自己设置布局
        MyListView view = new MyListView(UIUtils.getContext());


        // 给listview增加头布局展示轮播条
        HomeHeaderHolder header = new HomeHeaderHolder();
        view.addHeaderView(header.getRootView());// 先添加头布局,再setAdapter


        //给列表页设置数据
        //设置应该在添加之后，不能在添加之前，和上面那行代码换下顺序
        view.setAdapter(new HomeAdapter(data));
        if (mPictureList != null) {
            //一定要刷新view，所有要调用这个方法，底层把数据塞进去
            // 设置轮播条数据
            //这个data不对，是appinfo，这里要的是一堆字符串集合，在HomeProtocol里面解析
            header.setData(mPictureList);
        }

        //给listview设置点击事件
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //item条目在HomeFragment的appinfo知道包名，在这里获取
                AppInfo appInfo = data.get(position - 1);// 去掉头布局

                if (appInfo != null) {
                    //点击item事件，涉及到整个页面切换，就用到activity
                    Intent intent = new Intent(UIUtils.getContext(),HomeDetailActivity.class);
                    //item条目在HomeFragment的appinfo知道包名，在这里获取
                    intent.putExtra("packageName", appInfo.packageName);
                    startActivity(intent);
                }
            }
        });


        return view;
    }

    // 运行在子线程,可以直接执行耗时网络操作，
    // 最终加载数据在子类里实现的，这个onload在子线程里
    //进来先走这个方法，成功后，在走oncreaView这个方法
    @Override
    public ResultState onLoad() {

        //准备一个集合来测试用
        // data = new ArrayList<String>();
        // for (int i = 0; i < 20; i++) {
        // data.add("测试数据:" + i);
        // }

        // 请求网络, HttpClient, HttpUrlConnection, XUtils
        HomeProtocol protocol = new HomeProtocol();
        data = protocol.getData(0);// 加载第一页数据.index写0，返回网络数据


        //一定要刷新view，所有要调用这个方法，底层把数据塞进去
        // 设置轮播条数据
        //这个data不对，是appinfo，这里要的是一堆字符串集合，在HomeProtocol里面解析
        //这里暴漏一个方法，让其他方法能够获取到这个数据
        //把这个搞成全局的了
        mPictureList = protocol.getPictureList();

        //因为请求网络的data不一定有值，所有要对数据进行校验。所以在父类里面写了方法
        return check(data);// 校验数据并返回
    }

    //listView需要数据，这里就是填充数据的adapter
    class HomeAdapter extends MyBaseAdapter<AppInfo> {

        public HomeAdapter(ArrayList<AppInfo> data) {
            super(data);
        }

        //必须实现抽象方法，要返回具体的holder对象，要写一个子类来集成Baseholder
        @Override
        public BaseHolder<AppInfo> getHolder(int position) {
            return new HomeHolder();
        }

        // 此方法在子线程调用，因为MyBaseAdapter,在thread里面调用的onLoadMore
        @Override
        public ArrayList<AppInfo> onLoadMore() {
            //搞一些假数据
//             ArrayList<AppInfo> moreData = new ArrayList<AppInfo>();
//             for(int i=0;i<20;i++) {
//             moreData.add("测试更多数据:" + i);
//             }
//             //数据感觉真实还可以睡一下
//             SystemClock.sleep(2000);

            //下一页数据的方法
            HomeProtocol protocol = new HomeProtocol();
            //getData(index)的index不能是0，而且不能写死数字，当前集合的大小和需要传的值一样，所有获取当前集合的大小
            // （当前集合在baseAdapter维护着，要暴漏一个接口来返回当前集合的大小）
            ArrayList<AppInfo> moreData = protocol.getData(getListSize());

            return moreData;
        }

        /*******************************需要封装一下，细节不知道怎么实现让子类实现，需要搞一个BaseHolder****************************************/
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            //声明了一个对象来协助这类的操作
//            ViewHolder holder;
//            if (convertView == null) {
//                //1. 加载布局文件
//                convertView = UIUtils.inflate(R.layout.list_item_home);
//                holder = new ViewHolder();
//                //2. 初始化控件 findViewById
//                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
//
//                //3. 打一个标记tag
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            //4. 根据数据来刷新界面
//            String content = getItem(position);
//            holder.tvContent.setText(content);
//
//            return convertView;
//        }
    }

    static class ViewHolder {
        public TextView tvContent;
    }

}
