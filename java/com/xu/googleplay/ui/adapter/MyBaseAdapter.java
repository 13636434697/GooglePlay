package com.xu.googleplay.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.xu.googleplay.manager.ThreadManager;
import com.xu.googleplay.ui.holder.BaseHolder;
import com.xu.googleplay.ui.holder.MoreHolder;
import com.xu.googleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 对adapter的封装，对listView进行封装
 * 这里也要定义一个泛型，当时什么类型就用什么类型，T字随意填写
 * T就当做对象，就是个类
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    //声明2中布局类型
    //注意: 此处必须要从0开始写
    //因为这个值是传给adapter底层的，viewtype用的，可能是一个数组或者集合，都是从0开始读的
    private static final int TYPE_NORMAL = 1;// 正常布局类型
    private static final int TYPE_MORE = 0;// 加载更多类型

    // （当前集合在baseAdapter维护着，要暴漏一个接口来返回当前集合的大小）
    //全局的，声明一下，定义一个泛型
    private ArrayList<T> data;

    //构造方法，把集合传过来，这里使用的泛型，需要声明全局的
    //因为下面2个方法需要集合才能用，
    public MyBaseAdapter(ArrayList<T> data) {
        this.data = data;
    }

    //上面三个方法简单，可以直接写在父类的，没有必要子类实现，数据通过构造方法传过来
    @Override
    public int getCount() {
        return data.size() + 1;// 增加加载更多布局数量
    }

    //这里就返回泛型T
    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //要让listView展示多种布局，重写好二个方法，需要在数量上加1
    // 返回布局类型个数，将来返回几个，也可以重写这个方法
    @Override
    public int getViewTypeCount() {
        return 2;// 返回两种类型,普通布局+加载更多布局
    }
    //要让listView展示多种布局，重写好二个方法，需要在数量上加1
    // 返回当前位置应该展示那种布局类型
    @Override
    public int getItemViewType(int position) {
        //如果当前位置是最后一个
        if (position == getCount() - 1) {// 最后一个
            //这个就是加载更多
            return TYPE_MORE;
        } else {
            //在基类里面写，大家都要用他，这样写有局限性
            //return TYPE_NORMAL;
            //可能有2种以上的listView，这样就不能用TYPE_NORMAL来表示，要预留扩展路口，能够进行修改

            //因为CategoryFragment多了一种类型，所有重写getInnerType,但是要把当时的position传过去
            return getInnerType(position);
        }
    }

    // 子类可以重写此方法来更改返回的布局类型
    //这里没有必须抽象，因为大部分listView，正常写，少数才多种listView，
    // 区别的话，子类就可以重写这个方法，重写后，写什么由子类说了算

    //因为CategoryFragment多了一种类型，所有重写getInnerType,但是要把当时的position传过去
    public int getInnerType(int position) {
        return TYPE_NORMAL;// 默认就是普通类型
    }

    //又封装了一个BaseHolder
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //声明这个类，
        BaseHolder holder;
        if (convertView == null) {
            // 1. 加载布局文件
            // 2. 初始化控件 findViewById
            // 3. 打一个标记tag
            //根据getView，根据情况来返回不同的对象，如果在最后一个的话，判断是否是加载更多的类型
            if (getItemViewType(position) == TYPE_MORE) {
                // 加载更多的类型，在写一个holder，加载跟多的holder对象来初始化布局，凡是一个布局都要写一个holder
                //这里不用子类实现，因为MoreHolder都是这样的布局，因为加载更多都是一样的
                //需要有一个是否有更多数据的标记。
                // 区别的话，子类就可以重写这个方法，重写后，写什么由子类说了算
                holder = new MoreHolder(hasMore());
            } else {
                //当new BaseHolder这个对象时, 就会加载布局, 初始化控件,设置tag，但是 BaseHolder是不能直接new的，
                // 因为他是抽象类，要实现他的好几个方法，而这个方法怎么实现他根本不知道，实现要具体页面才知道，
                // 不知道该怎么实现的时候，就用抽象类，交给子类实现
                holder = getHolder(position);//初始化holder，但是不知道具体对象，就让子类返回具体对象
            }
        } else {
            //从convertView里面拿回标记就可以了
            holder = (BaseHolder) convertView.getTag();
        }

        //普通的对象刷新界面不需要判断，更多的话，也就集中，不需要刷新界面
        // 4. 根据数据来刷新界面
        if (getItemViewType(position) != TYPE_MORE) {
            //设置数据，普通的对象刷新界面，设置给Baseholder对象，设置之后就直接刷新界面了
            holder.setData(getItem(position));
        } else {
            // 加载更多布局显示
            // 一旦加载更多布局展示出来, 就开始加载更多数据
            //上面已经new过MoreHolder了，这强转一下就可以了。
            MoreHolder moreHolder = (MoreHolder) holder;
            // 只有在有更多数据的状态下才加载更多，getData()可以拿到具体的状态值
            if (moreHolder.getData() == MoreHolder.STATE_MORE_MORE) {
                // 加载更多数据
                loadMore(moreHolder);
            }
        }

        //应该返回布局，在holder类里面去拿
        return holder.getRootView();
    }

    //需要有一个是否有更多数据的标记。
    // 子类可以重写此方法来决定是否可以加载更多
    // 区别的话，子类就可以重写这个方法，重写后，写什么由子类说了算
    //没有必要搞成抽象的
    public boolean hasMore() {
        return true;// 默认都是有更多数据的
    }


    //当new BaseHolder这个对象时, 就会加载布局, 初始化控件,设置tag，但是 BaseHolder是不能直接new的，
    // 因为他是抽象类，要实现他的好几个方法，而这个方法怎么实现他根本不知道，实现要具体页面才知道，
    // 不知道该怎么实现的时候，就用抽象类，交给子类实现
    // 返回当前页面的holder对象, 必须子类实现
    public abstract BaseHolder<T> getHolder(int position);

    //为了这个方法只运行一次，所有要加个标记
    private boolean isLoadMore = false;// 标记是否正在加载更多

    //为了这个方法只运行一次，所有要加个标记
    //用户listView滑倒最低端的时候，显示加载更多，并要请求网络去加载更多数据
    // 加载更多数据
    public void loadMore(final MoreHolder holder) {
        //没有加载更多，才去加载
        if (!isLoadMore) {
            isLoadMore = true;

//            //起一个子线程来请求网络
//            new Thread() {
//                @Override
//                public void run() {
//                    //访问网络接口，请求下一页数据，刷新界面，访问网络接口的时候，BaseAdapter这个类是不知道，所以要让子类实现方法
//                    final ArrayList<T> moreData = onLoadMore();
//                    //刷新数据要在主线程的。所以在运行在主线程
//                    UIUtils.runOnUIThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            //返回的数据不等于空才有意义
//                            if (moreData != null) {
//                                // 每一页有20条数据, 如果返回的数据小于20条, 就认为到了最后一页了
//                                //不等于空，不一定表示完全成功，返回的数据小于20条，就没有跟多数据了
//                                //因为服务器一次就给20，这里就写死了。
//                                if (moreData.size() < 20) {
//                                    holder.setData(MoreHolder.STATE_MORE_NONE);
//                                    Toast.makeText(UIUtils.getContext(),"没有更多数据了", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    // 还有更多数据，就在去请求
//                                    holder.setData(MoreHolder.STATE_MORE_MORE);
//                                }
//
//                                //这里已经有数据了，需要加在arrayList集合中
//                                // 将更多数据追加到当前集合中
//                                data.addAll(moreData);
//                                // 刷新界面，不能直接用this，因为这个是内部类
//                                MyBaseAdapter.this.notifyDataSetChanged();
//                            } else {
//                                // 数据为空的，表面加载更多失败，要根据失败的结果，要刷加载失败的界面，所以参数要传出去
//                                //刷新数据要在主线程的。所以在运行在主线程
//                                holder.setData(MoreHolder.STATE_MORE_ERROR);
//                            }
//                            //不管数据加载成功失败，都结束了
//                            isLoadMore = false;
//                        }
//                    });
//                }
//            }.start();

            //下拉加载跟多，也是子线程，也改造成线程池，run里面的方法一样
            ThreadManager.getThreadPool().execute(new Runnable() {

                @Override
                public void run() {
                    final ArrayList<T> moreData = onLoadMore();

                    UIUtils.runOnUIThread(new Runnable() {

                        @Override
                        public void run() {
                            if (moreData != null) {
                                // 每一页有20条数据, 如果返回的数据小于20条, 就认为到了最后一页了
                                if (moreData.size() < 20) {
                                    holder.setData(MoreHolder.STATE_MORE_NONE);
                                    Toast.makeText(UIUtils.getContext(),
                                            "没有更多数据了", Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    // 还有更多数据
                                    holder.setData(MoreHolder.STATE_MORE_MORE);
                                }

                                // 将更多数据追加到当前集合中
                                data.addAll(moreData);
                                // 刷新界面
                                MyBaseAdapter.this.notifyDataSetChanged();
                            } else {
                                // 加载更多失败
                                holder.setData(MoreHolder.STATE_MORE_ERROR);
                            }

                            isLoadMore = false;
                        }
                    });
                }
            });
        }

    }

    //访问网络接口的时候，BaseAdapter这个类是不知道，所以要让子类实现方法
    // 加载更多数据, 必须由子类实现,为了拿到这个数据，必须要返回更多的数据，肯定是一个集合，泛型
    public abstract ArrayList<T> onLoadMore();

    // （当前集合在baseAdapter维护着，要暴漏一个接口来返回当前集合的大小）
    //获取当前集合大小
    public int getListSize() {
        return data.size();
    }

}

