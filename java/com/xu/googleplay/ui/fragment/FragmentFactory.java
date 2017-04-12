package com.xu.googleplay.ui.fragment;


import java.util.HashMap;

/**
 * 生产fragment工厂，使用到了工厂的设计模式
 */
public class FragmentFactory {

    //每次生产都new一个，是没有必要的，只需要new一次。所以用到一个集合把fragment维护起来，
    // HashMap<Integer, BaseFragment>，Integer是当前的位置，BaseFragment对象
    private static HashMap<Integer, BaseFragment> mFragmentMap = new HashMap<Integer, BaseFragment>();

    public static BaseFragment createFragment(int pos) {
        // 先从集合中取, 如果没有,才创建对象, 提高性能
        BaseFragment fragment = mFragmentMap.get(pos);

        if (fragment == null) {
            switch (pos) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new AppFragment();
                    break;
                case 2:
                    fragment = new GameFragment();
                    break;
                case 3:
                    fragment = new SubjectFragment();
                    break;
                case 4:
                    fragment = new RecommendFragment();
                    break;
                case 5:
                    fragment = new CategoryFragment();
                    break;
                case 6:
                    fragment = new HotFragment();
                    break;

                default:
                    break;
            }

            mFragmentMap.put(pos, fragment);// 将fragment保存在集合中
        }

        return fragment;
    }
}
