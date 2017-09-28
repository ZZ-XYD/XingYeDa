package com.xingyeda.ehome;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

/**
 * @ClassName: ActivityExplain
 * @Description: 使用说明页面
 * @author 李达龙
 * @date 2016-7-6
 */
public class ActivityExplain extends BaseActivity{

   @Bind(R.id.explain_image)
    ImageView mExplainImage;
   @Bind(R.id.explain_btn)
    Button mExplainButton;
    private String mType;
    
    private int[] mIamge={R.mipmap.explain1,R.mipmap.explain2,R.mipmap.explain3,R.mipmap.explain4,R.mipmap.explain5,R.mipmap.explain6};
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        SharedPreUtil.put(mContext, "isFirstExplain", false);
        ButterKnife.bind(this);
        mType = getIntent().getExtras().getString("type");
        mExplainImage.setScaleType(ScaleType.FIT_XY);
        setImage();
        
    }
    private void setImage() {
	mExplainImage.setImageResource(mIamge[count]);
//	mExplainImage.setImageBitmap(bitmap);
    }
    @OnClick(R.id.explain_btn)
    public void onClick(){
	if ((count+1)>= mIamge.length) {
		if (mType.equals("about")) {
			ActivityExplain.this.finish();
		}else if (mType.equals("login")) {
			BaseUtils.startActivity(mContext, ActivityHomepage.class);
		}
	}
	else {
	    count++;
	    setImage();
	}
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
