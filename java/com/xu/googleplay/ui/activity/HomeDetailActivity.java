package com.xu.googleplay.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.xu.googleplay.R;
import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.http.protocol.HomeDetailProtocol;
import com.xu.googleplay.ui.holder.DetailAppInfoHolder;
import com.xu.googleplay.ui.holder.DetailDesHolder;
import com.xu.googleplay.ui.holder.DetailDownloadHolder;
import com.xu.googleplay.ui.holder.DetailPicsHolder;
import com.xu.googleplay.ui.holder.DetailSafeHolder;
import com.xu.googleplay.ui.view.LoadingPage;
import com.xu.googleplay.utils.UIUtils;


/**
 * 首页应用详情页
 *
 * 没有网络的情况下，是加载失败，在Fragment里面有过，所以复用下LoadingPage
 */
public class HomeDetailActivity extends BaseActivity {

    private LoadingPage mLoadingPage;
    private String packageName;
    private AppInfo data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //没有网络的情况下，是加载失败，在Fragment里面有过，所以复用下
        //LoadingPage有加载视图的方法
        mLoadingPage = new LoadingPage(this) {

            @Override
            public ResultState onLoad() {
                //调用类里面的方法
                //这个流程和baseFragment的流程几乎是一样的，
                // 区别在于，下面的方法是抽象的（因为父类不知道怎么做，就交给子类了），在这里就直接实现了
                return HomeDetailActivity.this.onLoad();
            }

            //加载成功的方法
            @Override
            public View onCreateSuccessView() {
                //调用类里面的方法
                return HomeDetailActivity.this.onCreateSuccessView();
            }
        };

        // 直接将一个view对象设置给activity
        // setContentView(R.layout.activity_main);
        setContentView(mLoadingPage);

        //item条目在HomeFragment的appinfo知道包名
        // 获取从HomeFragment传递过来的包名
        packageName = getIntent().getStringExtra("packageName");

        // 开始加载网络数据，这个方法调用父类的方法，父类在调用抽象方法在回来调用这里的加载网络
        mLoadingPage.loadData();
        initActionbar();
    }

    // 初始化actionbar
    private void initActionbar() {
        ActionBar actionbar = getSupportActionBar();
        // actionbar.setHomeButtonEnabled(true);// home处可以点击
        actionbar.setDisplayHomeAsUpEnabled(true);// 显示左上角返回键,当和侧边栏结合时展示三个杠图片
    }
    //点击方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //返回键也是home
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //不能直接往下拉，需要往下拉的话，需要ScrollView来包裹起来
    //父类的2个方法，在这里写
    //下面方法成功后，就会走这里的方法，然后就开始初始化布局
    public View onCreateSuccessView() {
        //整体分成四部分，开始封装每一个部分都单独封装到对象里面，先搭一个框架
        // 初始化成功的布局
        View view = UIUtils.inflate(R.layout.page_home_detail);

        // 初始化应用信息模块，拿到幀布局
        FrameLayout flDetailAppInfo = (FrameLayout) view.findViewById(R.id.fl_detail_appinfo);

        // 动态给帧布局填充页面
        DetailAppInfoHolder appInfoHolder = new DetailAppInfoHolder();
        //给幀布局添加布局，拿到布局
        flDetailAppInfo.addView(appInfoHolder.getRootView());
        //数据也要设置进去，数据在onLoad能拿到data
        appInfoHolder.setData(data);


        // 初始化安全描述模块（封装到了一个类里面）
        FrameLayout flDetailSafe = (FrameLayout) view.findViewById(R.id.fl_detail_safe);
        DetailSafeHolder safeHolder = new DetailSafeHolder();
        //给幀布局添加布局，拿到布局
        flDetailSafe.addView(safeHolder.getRootView());
        //刷新数据
        safeHolder.setData(data);


        // 初始化截图模块
        HorizontalScrollView hsvPic = (HorizontalScrollView) view.findViewById(R.id.hsv_detail_pics);
        //初始化一个holder，因为在holder对象里处理逻辑
        DetailPicsHolder picsHolder = new DetailPicsHolder();
        //在这里要添加截图页面holder，拿到根部局.getRootView()
        hsvPic.addView(picsHolder.getRootView());
        //还要设置数据
        picsHolder.setData(data);

        // 初始化描述模块
        FrameLayout flDetailDes = (FrameLayout) view.findViewById(R.id.fl_detail_des);
        DetailDesHolder desHolder = new DetailDesHolder();
        flDetailDes.addView(desHolder.getRootView());
        desHolder.setData(data);

        //给图片设置点击事件，就是一个大的viewPager，问题就是跳页面的时候需要知道链接，
        // 通过intent来传递参数data.screen，用bitmap重写加载图片。ivPics[i]可以传当前默认的点击的图片
        //获取序列化的集合list就拿到了
        // getIntent().getSerializableExtra("list");


        // 初始化下载模块
        FrameLayout flDetailDownload = (FrameLayout) view.findViewById(R.id.fl_detail_download);
        DetailDownloadHolder downloadHolder = new DetailDownloadHolder();
        flDetailDownload.addView(downloadHolder.getRootView());
        downloadHolder.setData(data);
        return view;
    }


    //父类的2个方法，在这里写
    //在这里就加载网络，请求数据，这个成功之后就会走上面的方法
    public LoadingPage.ResultState onLoad() {
        // 请求网络,加载数据
        HomeDetailProtocol protocol = new HomeDetailProtocol(packageName);
        //拿到第一页就可以了，没有分页
        data = protocol.getData(0);

        if (data != null) {
            //数据不等于，就是成功了
            return LoadingPage.ResultState.STATE_SUCCESS;
        } else {
            //数据空，就是失败了
            return LoadingPage.ResultState.STATE_ERROR;
        }
    }
}
