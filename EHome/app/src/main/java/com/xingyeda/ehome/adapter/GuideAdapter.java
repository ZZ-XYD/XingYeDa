package com.xingyeda.ehome.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class GuideAdapter extends PagerAdapter{
	private List<View> list;
	
	public GuideAdapter(List<View> list){
		this.list=list;
	}
	/** ���ÿؼ�����ʾ���������*/
	@Override
	public int getCount() {
		return list.size();
	}
	/** �ж��Ƿ��ɶ�����ɽ���*/
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==(View)arg1;
	}
	/**���positionλ�õĽ���*/
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(list.get(position));
	}
	/** ��ʼ��positionλ�õĽ���*/
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view=list.get(position);
		container.addView(view);
		return view;
	}
	
	
	
}
