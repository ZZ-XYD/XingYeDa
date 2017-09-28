package com.xingyeda.ehome.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

// 多页面视图控件适配器
public class AdapterGuidePager extends PagerAdapter {
    
    private ViewPager mViewPager;
    private List<ImageView> mImagelist;// 页面需要的图片控件
    public AdapterGuidePager(ViewPager viewPager,List<ImageView> list){
        this.mViewPager = viewPager;
        this.mImagelist = list;
    }
    // 实例化页面
    @Override
    public Object instantiateItem(View container, int position) {
        View view = mImagelist.get(position);// 获取要显示的View，用position作为坐标在集合内找iv
        mViewPager.addView(view);// 手动添加控件
        return view;// 返回此控件
    }

    // 销毁页面
    @Override
    public void destroyItem(View container, int position, Object object) {
        View view = mImagelist.get(position);
        mViewPager.removeView(view);// 手动删除控件
    }

    // 获取页面数
    @Override
    public int getCount() {
        return mImagelist.size();// 集合长度
    }

    // 判断需要显示的页面是否为缓存页，是则不会创建新对象。
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }


}