package com.xu.googleplay.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xu.googleplay.ui.view.LoadingPage.ResultState;
import com.xu.googleplay.ui.view.LoadingPage;
import com.xu.googleplay.utils.UIUtils;

import java.util.ArrayList;

public abstract class BaseFragment extends Fragment {

    private LoadingPage mLoadingPage;

    //fragment加载布局需要重写这个方法
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //所有有共性的页面都在这里加载
        mLoadingPage = new LoadingPage(UIUtils.getContext()){

            //BaseFragment是不知道当前页面该怎么去布局的，因为是父类不会知道子类应该怎么去实现的，
            // 但是loadingpager问题就交给了这个类，所以在让子类实现，在写一个抽象的方法
            @Override
            public View onCreateSuccessView() {
                // 注意:此处一定要调用BaseFragment的onCreateSuccessView, 否则栈溢出
                //因为2个类的方法做的是同一件事情，所有命名成一样的，下面方法的名字是可以改其他名字的

                //加载完布局逻辑之后，还要加载数据，变成加载中的状态，并开启一个子线程来加载网络来请求数据，
                // 放在loadingpager里实现，这样子类就没有必要实现了
                //这个类也不知道怎么去实现，所有又交给的子类去实现
                return BaseFragment.this.onCreateSuccessView();
            }
            //加载完布局逻辑之后，还要加载数据，变成加载中的状态，并开启一个子线程来加载网络来请求数据，
            // 放在loadingpager里实现，这样子类就没有必要实现了
            @Override
            public ResultState onLoad() {
                return BaseFragment.this.onLoad();
            }

        };

        return mLoadingPage;
    }

    // loadingpager问题就交给了这个类，所以在让子类实现，在写一个抽象的方法
    // 加载成功的布局, 必须由子类来实现
    public abstract View onCreateSuccessView();

    // loadingpager问题就交给了这个类，所以在让子类实现，在写一个抽象的方法
    // 加载网络数据, 必须由子类来实现
    public abstract ResultState onLoad();

    // 主页面调用，并开始加载数据
    public void loadData() {
        if (mLoadingPage != null) {
            mLoadingPage.loadData();
        }
    }

    // 对网络返回数据的合法性进行校验
    //穿进来一个数据，可能是一个任何类型的数据
    public ResultState check(Object obj) {
        if (obj != null) {
            // 判断是否是集合
            if (obj instanceof ArrayList) {
                //是一个集合就强转成集合
                ArrayList list = (ArrayList) obj;
                //对这个集合在判断是否为空
                if (list.isEmpty()) {
                    return ResultState.STATE_EMPTY;
                } else {
                    return ResultState.STATE_SUCCESS;
                }
            }
        }
        //所有其他情况都认为是失败
        return ResultState.STATE_ERROR;
    }
}
