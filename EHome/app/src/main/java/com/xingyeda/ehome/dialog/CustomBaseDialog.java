package com.xingyeda.ehome.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;

import com.ldl.dialogshow.animation.Attention.Swing;
import com.ldl.dialogshow.dialog.utils.CornerUtils;
import com.ldl.dialogshow.dialog.widget.base.BaseDialog;
import com.xingyeda.ehome.R;

public class CustomBaseDialog extends BaseDialog<CustomBaseDialog> {
   @Bind(R.id.cancel) TextView mTvCancel;
   @Bind(R.id.content) TextView mTvContent;
    
    private String mContent = "";
//    private String mCancel = "";

    public CustomBaseDialog(Context context) {
        super(context);
    }
    public CustomBaseDialog(Context context,String content) {
    	super(context);
    	mContent = content;
//    	mCancel = cancel;
    	
    }

    @SuppressWarnings("deprecation")
	@Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new Swing());

        View inflate = View.inflate(mContext, R.layout.dialog_custom_base, null);
        ButterKnife.bind(this, inflate);
        mTvContent.setText(mContent);
//        mTvCancel.setText(mCancel);
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));

        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
