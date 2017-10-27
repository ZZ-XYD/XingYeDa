package com.xingyeda.ehome.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.xingyeda.ehome.bean.AdvertisementBean;

import java.util.List;


public class AdvertisingAdapter extends LoopPagerAdapter {

    private List<AdvertisementBean> mList;

    public AdvertisingAdapter(RollPagerView viewPager, List<AdvertisementBean> mList) {
        super(viewPager);
        this.mList = mList;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setImageBitmap(mList.get(position).getmBitmap());
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public int getRealCount() {
        return mList.size();
    }
}