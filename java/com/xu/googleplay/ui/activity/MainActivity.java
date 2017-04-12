package com.xu.googleplay.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.xu.googleplay.R;
import com.xu.googleplay.ui.fragment.BaseFragment;
import com.xu.googleplay.ui.fragment.FragmentFactory;
import com.xu.googleplay.ui.view.PagerTab;
import com.xu.googleplay.utils.UIUtils;

/**
 * 当项目和appcompat关联在一起时, 就必须在清单文件中设置Theme.AppCompat的主题, 否则崩溃
 * android:theme="@style/Theme.AppCompat.Light"
 *
 *  所有的activity都要继承这个activity
 *
 *
 *  整体布局就是一个抽屉的布局，侧边栏
 */
public class MainActivity extends BaseActivity {

    private PagerTab mPagerTab;
    private ViewPager mViewPager;
    private MyAdapter mAdapter;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPagerTab = (PagerTab) findViewById(R.id.pager_tab);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        //多了一个构造方法，拿到FragmentManager()，在activity拿到FragmentManager()，一般拿不到的
        // 必须集成fragmentactivity，因为爷爷就是fragmentactivity，所有直接getSupportFragmentManager()
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        //标签页和viewpager绑定在一起
        mPagerTab.setViewPager(mViewPager);// 将指针和viewpager绑定在一起

        //页面切换的事件监听，因为mViewPager和mPagerTab绑定在一起，所有要设置监听给mPagerTab
        mPagerTab.setOnPageChangeListener(new OnPageChangeListener() {
            //继承基本都是多态的概念
            @Override
            public void onPageSelected(int position) {
                //当某个页面被选中，根据位置拿到页面对象
                BaseFragment fragment = FragmentFactory
                        .createFragment(position);
                // 开始加载数据 BaseFragment
                fragment.loadData();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        initActionbar();
    }

    // 初始化actionbar
    private void initActionbar() {
        //调用他就没有版本兼容的问题了
        ActionBar actionbar = getSupportActionBar();

        actionbar.setHomeButtonEnabled(true);// home处可以点击
        actionbar.setDisplayHomeAsUpEnabled(true);// 显示左上角返回键,当和侧边栏结合时展示三个杠图片

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);

        //侧边栏和其他控件没有关系，需要关联
        // 初始化抽屉开关
        //参数：根部局，返回键的资源，打开抽屉的文字描述
        //toggle = new ActionBarDrawerToggle(this, drawer,R.drawable.ic_drawer_am,R.string.drawer_open,R.string.drawer_close);
        toggle = new ActionBarDrawerToggle(this, drawer,R.string.drawer_open,R.string.drawer_close);

        toggle.syncState();// 同步状态, 将DrawerLayout和开关关联在一起
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 切换抽屉
                toggle.onOptionsItemSelected(item);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * 在viewpager放fragment，需要给viewpager填充数据
     *
     * FragmentPagerAdapter是PagerAdapter的子类, 如果viewpager的页面是fragment的话,就继承此类
     */
    class MyAdapter extends FragmentPagerAdapter {
        //标签名称的初始化
        private String[] mTabNames;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            //需要加载标签的名称
            mTabNames = UIUtils.getStringArray(R.array.tab_names);// 加载页面标题数组
        }

        // 返回页签标题，拿到当前的自定义空间的标题
        @Override
        public CharSequence getPageTitle(int position) {
            //到数组里面去取
            return mTabNames[position];
        }

        // 返回当前页面位置的fragment对象
        @Override
        public Fragment getItem(int position) {
            //生产fragment
            BaseFragment fragment = FragmentFactory.createFragment(position);
            return fragment;
        }

        // fragment数量
        @Override
        public int getCount() {
            return mTabNames.length;
        }

    }

}