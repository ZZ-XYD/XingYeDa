package com.xingyeda.ehome.zhibo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.util.BaseUtils;


/**
 * <p>Title:PopWindow</p>
 * <p>Description: 自定义PopupWindow</p>
 * @author syz
 * @date 2016-3-14
 */
@SuppressLint("ViewConstructor") public class SightseerPopWindow extends PopupWindow{
	private View conentView;
	private Context mContext;
	public SightseerPopWindow(final Activity context){
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.popup_window, null);
		@SuppressWarnings({ "deprecation", "unused" })
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		@SuppressWarnings("deprecation")
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置SelectPicPopupWindow的View
		this.setContentView(conentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w / 2);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationPreview);
		final Bundle bundle = new Bundle();
		conentView.findViewById(R.id.my_share).setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View arg0) {
				hint();
				SightseerPopWindow.this.dismiss();
			}
		});
		conentView.findViewById(R.id.seek).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				BaseUtils.startActivity(mContext,ActivityShareSeek.class);
				SightseerPopWindow.this.dismiss();
			}
		});
	}
	private void hint(){
		final NormalDialog dialog = DialogShow.showSelectDialog(mContext,"此功能暂无权限，是否登录？",2,new String[] { mContext.getResources().getString(R.string.cancel),mContext.getResources().getString(R.string.confirm)});
		dialog.setOnBtnClickL(new OnBtnClickL() {

			@Override
			public void onBtnClick() {
				dialog.dismiss();
			}
		},new OnBtnClickL() {
			@Override
			public void onBtnClick() {
				BaseUtils.startActivity(mContext, ActivityLogin.class);
			}
		});
	}
	
	/**
	 * 显示popupWindow
	 * 
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			// 以下拉方式显示popupwindow
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);

		} else {
			this.dismiss();
		}
	}
}
