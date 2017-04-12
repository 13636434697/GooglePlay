package com.xu.googleplay.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xu.googleplay.R;
import com.xu.googleplay.manager.ThreadManager;
import com.xu.googleplay.utils.UIUtils;

/** 自定义控件，使用的是幀布局，上来就添加多个布局，然后在根据页面的状态来设置页面显示隐藏
 *  所有有共性的页面都在这里加载
 *
 * 根据当前状态来显示不同页面的自定义控件
 *
 * - 未加载 - 加载中 - 加载失败 - 数据为空 - 加载成功
 */
public abstract class LoadingPage extends FrameLayout {

    //声明加载的几种状态
    private static final int STATE_LOAD_UNDO = 1;// 未加载
    private static final int STATE_LOAD_LOADING = 2;// 正在加载
    private static final int STATE_LOAD_ERROR = 3;// 加载失败
    private static final int STATE_LOAD_EMPTY = 4;// 数据为空
    private static final int STATE_LOAD_SUCCESS = 5;// 加载成功

    private int mCurrentState = STATE_LOAD_UNDO;// 当前状态

    private View mLoadingPage;
    private View mErrorPage;
    private View mEmptyPage;
    private View mSuccessPage;


    //FrameLayout的构造方法的重写，来加载新的布局
    public LoadingPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 上来就调用这个方法，初始化加载中的布局
        initView();
    }

    //FrameLayout的构造方法的重写，来加载新的布局
    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 上来就调用这个方法，初始化加载中的布局
        initView();
    }

    //FrameLayout的构造方法的重写，来加载新的布局
    public LoadingPage(Context context) {
        super(context);
        // 上来就调用这个方法，初始化加载中的布局
        initView();
    }

    // 初始化加载中的布局
    private void initView() {
        //布局只需要初始化一次，所有先判断有没有，如果有就没有必要初始化了
        if (mLoadingPage == null) {
            //2张图叠加在一起，同时转，而且是方向相反
            mLoadingPage = UIUtils.inflate(R.layout.page_loading);
            // 将加载中的布局添加给当前的帧布局（FrameLayout）
            addView(mLoadingPage);
        }

        // 初始化加载失败布局
        if (mErrorPage == null) {
            mErrorPage = UIUtils.inflate(R.layout.page_error);
            // 加载网络失败，点击重试事件
            Button btnRetry = (Button) mErrorPage.findViewById(R.id.btn_retry);
            btnRetry.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 重新加载网络数据
                    loadData();
                }
            });

            addView(mErrorPage);
        }

        // 初始化数据为空布局
        if (mEmptyPage == null) {
            mEmptyPage = UIUtils.inflate(R.layout.page_empty);
            addView(mEmptyPage);
        }

        //因为加载成功，不知道该怎么初始化，需要等子类去实现
        // 所有根据当前状态,决定显示哪个布局
        showRightPage();
    }

    // 根据当前状态,决定显示哪个布局
    //这里就是更新主界面的UI
    private void showRightPage() {
        // if (mCurrentState == STATE_LOAD_UNDO
        // || mCurrentState == STATE_LOAD_LOADING) {
        // mLoadingPage.setVisibility(View.VISIBLE);
        // } else {
        // mLoadingPage.setVisibility(View.GONE);
        // }
        /*****************************不用的上面的方法，用下面的三元表达式*******************************************/
        //未加载页面，判断(mCurrentState == STATE_LOAD_UNDO || mCurrentState == STATE_LOAD_LOADING)是否满足条件，满足就是前面，不满足就是后面
        mLoadingPage.setVisibility((mCurrentState == STATE_LOAD_UNDO || mCurrentState == STATE_LOAD_LOADING) ? View.VISIBLE : View.GONE);
        //失败页面，判断mCurrentState == STATE_LOAD_ERROR是否满足条件，满足就是前面，不满足就是后面
        mErrorPage.setVisibility(mCurrentState == STATE_LOAD_ERROR ? View.VISIBLE : View.GONE);
        //数据为空的页面，判断mCurrentState == STATE_LOAD_ERROR是否满足条件，满足就是前面，不满足就是后面
        mEmptyPage.setVisibility(mCurrentState == STATE_LOAD_EMPTY ? View.VISIBLE : View.GONE);

        //加载成功的布局
         //当成功布局为空,并且当前状态为成功,才初始化成功的布局（这里只是初始化）
        if (mSuccessPage == null && mCurrentState == STATE_LOAD_SUCCESS) {
            //调用的是子类的实现方法，子类的方法是用来初始化布局的
            mSuccessPage = onCreateSuccessView();
            //当成功布局不等于空，为什么要判断，因为子类可能返回null
            if (mSuccessPage != null) {
                //就把这个布局就添加给幀布局
                addView(mSuccessPage);
            }
        }
        //成功的要显示的界面（上面只是初始化，这里要显示或者隐藏）
        if (mSuccessPage != null) {
            //数据为空的页面，判断mCurrentState == STATE_LOAD_ERROR是否满足条件，满足就是前面，不满足就是后面
            mSuccessPage.setVisibility(mCurrentState == STATE_LOAD_SUCCESS ? View.VISIBLE : View.GONE);
        }
    }

    //BaseFragment的onCreateSuccessView()加载完布局逻辑之后，还要加载数据，变成加载中的状态，
    // 并开启一个子线程来加载网络来请求数据，放在loadingpager里实现，这样子类就没有必要实现了
    // 开始加载网络数据
    //这个方法就是在页面切换哪里就是哪里调用
    public void loadData() {
        //在调这个方法之前还要判断当前的状态是不是在加载中，如果当前没有加载, 就开始加载数据
        if (mCurrentState != STATE_LOAD_LOADING) {
            //把当前的状态切换为正在加载中
            mCurrentState = STATE_LOAD_LOADING;

//            //异步请求网络，需要一个子线程
//            new Thread() {
//                @Override
//                public void run() {
//                    //在这里接受网络数据返回的状态,加载数据必须要有一个onload方法，就声明了一个抽象类，给BaseFragment实现
//                    //接受到子类返回的状态，根据对应状态来更新
//                    final ResultState resultState = onLoad();
//
//                    // 运行在主线程
//                    //showRightPage()这里就是更新主界面的UI，会挂掉的，所以这个方法要在主线程里
//                    UIUtils.runOnUIThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            //在子线程里请求网络，但是父类不会知道请求链接和数据，只有子类才知道，
//                            // 所以也要暴漏一个抽象的方法，让子类去实现
//                            if (resultState != null) {
//                                //在这个枚举里面可以拿到整数的那个值，这个值就是最终的一个结果，要更新当前的网络状态
//                                mCurrentState = resultState.getState();// 网络加载结束后,更新网络状态
//                                // 根据最新的状态来刷新页面，mCurrentState变了，在根据mCurrentState来决定谁显示谁隐藏
//                                //这里就是更新主界面的UI，会挂掉的，所以这个方法要在主线程里
//                                showRightPage();
//                            }
//                        }
//                    });
//                }
//            }.start();

            //加载列表需要线程，现在不用子线程，改造了线程池了，run里面的方法是一样的和上面
            ThreadManager.getThreadPool().execute(new Runnable() {

                @Override
                public void run() {
                    final ResultState resultState = onLoad();

                    // 运行在主线程
                    UIUtils.runOnUIThread(new Runnable() {

                        @Override
                        public void run() {
                            if (resultState != null) {
                                mCurrentState = resultState.getState();// 网络加载结束后,更新网络状态
                                // 根据最新的状态来刷新页面
                                showRightPage();
                            }
                        }
                    });
                }
            });
        }
    }

    // 加载成功后显示的布局, 必须由调用者来实现
    public abstract View onCreateSuccessView();

    // 加载网络数据, 需要返回值，表示请求网络结束后的状态，知道状态后就可以，做出相应的处理，这个返回值就用枚举的方法来获取
    public abstract ResultState onLoad();

    //加载完布局之后，加载数据，然后子类在返回状态，状态可以用int值来表示，这里用枚举的方法来表示状态
    public enum ResultState {
        //声明几个参数，网络的状态就三个
        //下面三个参数这个是一个类，是一个对象，是一种简化的形式，给对象加了构造方法，并且加了参数
        STATE_SUCCESS(STATE_LOAD_SUCCESS), //成功
        STATE_EMPTY(STATE_LOAD_EMPTY), //空
        STATE_ERROR(STATE_LOAD_ERROR);//失败

        //把值保存一下，当作一个类
        private int state;
        //下面三个参数这个是一个类，是一个对象，是一种简化的形式，给对象加了构造方法，并且加了参数
        //构造方法必须要私有的，不能在外面new，只能在内部new
        private ResultState(int state) {
            this.state = state;
        }

        //在暴漏一个方法，在外面拿到当前的状态，在返回出去
        public int getState() {
            return state;
        }
    }

    /****************************声明了几个成员变量，相当于枚举的简化*****************************************************/

    //声明了几个成员变量，相当于枚举的简化
//    public static class Person {
//        public static Person P1 = new Person(10);
//        public static Person P2 = new Person(12);
//        public static Person P3 = new Person(19);
//
//        public Person(int age) {
//
//        }
//    }
//
//     public enum Person {
//     P1,P2,P3;
//     }

}
