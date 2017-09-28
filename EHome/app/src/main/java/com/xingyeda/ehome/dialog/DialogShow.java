package com.xingyeda.ehome.dialog;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.WindowManager;

import com.ldl.dialogshow.animation.BaseAnimatorSet;
import com.ldl.dialogshow.animation.BounceEnter.BounceTopEnter;
import com.ldl.dialogshow.animation.SlideExit.SlideBottomExit;
import com.ldl.dialogshow.dialog.entity.DialogMenuItem;
import com.ldl.dialogshow.dialog.widget.MaterialDialog;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.ldl.dialogshow.dialog.widget.NormalListDialog;


public class DialogShow {
	
	   private static BaseAnimatorSet mBasIn;
	    private static BaseAnimatorSet mBasOut;
//
//	    public void setBasIn(BaseAnimatorSet bas_in) {
//	        this.mBasIn = bas_in;
//	    }
//
//	    public void setBasOut(BaseAnimatorSet bas_out) {
//	        this.mBasOut = bas_out;
//	    }
	public static  void showHintDialog(Context context,String content){
		 CustomBaseDialog dialog = new CustomBaseDialog(context, content);
         dialog.show();
         dialog.setCanceledOnTouchOutside(false);
	}
	public static NormalListDialog showListDialog(Context context, ArrayList<DialogMenuItem> list){
		  NormalListDialog dialog = new NormalListDialog(context, list);
        dialog.title("请选择")//
                .isTitleShow(false)//
                .itemPressColor(Color.parseColor("#85D3EF"))//
                .itemTextColor(Color.parseColor("#303030"))//
                .itemTextSize(15)//
                .cornerRadius(2)//
                .widthScale(0.75f)//
                .show();
        return dialog;
	}
	public static NormalDialog  showSelectDialog(Context context,String title,String content){
		mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();
		NormalDialog dialog = new NormalDialog(context);
		dialog
		.title(title)
		.content(content)
        .style(NormalDialog.STYLE_TWO)
        .titleTextSize(23)
        .showAnim(mBasIn)
        .dismissAnim(mBasOut)
        .show();
		return dialog;
	}
	public static NormalDialog  showSelectDialogONE(Context context,String title,String content){
		mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();
		NormalDialog dialog = new NormalDialog(context);
//		dialog
//		.title(title)
//		.content(content)
//		.btnNum(1)
//        .style(NormalDialog.STYLE_ONE)
//        .titleTextSize(23)
//        .showAnim(mBasIn)
//        .dismissAnim(mBasOut)
//        .show();
		
		dialog
		.title(title)
		.content(content)//
        .btnNum(1)
        .btnText("确定")//
        .showAnim(mBasIn)//
        .dismissAnim(mBasOut)//
        .show();


		

		return dialog;
	}
	public static NormalDialog  showSelectDialog(Context context,String content){
		mBasIn = new BounceTopEnter();
		mBasOut = new SlideBottomExit();
		NormalDialog dialog = new NormalDialog(context);
		dialog
		.isTitleShow(false)
		.content(content)
		.style(NormalDialog.STYLE_TWO)
		.titleTextSize(23)
		.showAnim(mBasIn)
		.dismissAnim(mBasOut)
		.show();
		return dialog;
	}
	public static NormalDialog  showSelectDialog(Context context,String content, int btnNum,String[] btnTexts){
		mBasIn = new BounceTopEnter();
		mBasOut = new SlideBottomExit();
		NormalDialog dialog = new NormalDialog(context);
		dialog
		.isTitleShow(false)
		.btnNum(btnNum)
		.btnText(btnTexts)
		.content(content)
		.style(NormalDialog.STYLE_TWO)
		.titleTextSize(23)
		.showAnim(mBasIn)
		.dismissAnim(mBasOut)
		.show();
		return dialog;
	}
	public static NormalDialog  showSelectDialog(Context context,String title,String content, int btnNum,String[] btnTexts){
		mBasIn = new BounceTopEnter();
		mBasOut = new SlideBottomExit();
		NormalDialog dialog = new NormalDialog(context);
		dialog
		.title(title)
		.btnNum(btnNum)
		.btnText(btnTexts)
		.content(content)
		.style(NormalDialog.STYLE_TWO)
		.titleTextSize(23)
		.showAnim(mBasIn)
		.dismissAnim(mBasOut)
		.show();
		return dialog;
	}
	public static MaterialDialog showMessageDialog(Context context, String title, String content, int btnNum, String[] btnTexts){
		  MaterialDialog dialog = new MaterialDialog(context);
	        dialog.btnNum(btnNum)
	                .title(title)
	                .content(content)//
	                .btnText(btnTexts)//
	                .showAnim(mBasIn)//
	                .dismissAnim(mBasOut)//
	                .show();

	        
		return dialog;
	}


}
