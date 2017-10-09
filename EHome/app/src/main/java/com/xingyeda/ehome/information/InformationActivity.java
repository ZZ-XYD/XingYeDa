package com.xingyeda.ehome.information;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.LogUtils;

@SuppressLint("HandlerLeak")
public class InformationActivity extends BaseActivity {

   @Bind(R.id.news_back)
    TextView mBack;
   @Bind(R.id.news_title)
    TextView mTitle;
   @Bind(R.id.news_title_text)
    TextView mTitleShow;
   @Bind(R.id.news_time_text)
    TextView mTime;
   @Bind(R.id.news_content)
    TextView mContent;
   @Bind(R.id.news_message_status)
    TextView mMessageStatusShow;
   @Bind(R.id.news_door_status)
    TextView mDoorStatusShow;
   @Bind(R.id.news_initiator)
    TextView mInitiatorShow;
   @Bind(R.id.news_receiver)
    TextView mReceiverShow;
   @Bind(R.id.news_image)
    ImageView mImage;
   @Bind(R.id.message_status)
    LinearLayout mMessageStatus;
   @Bind(R.id.door_status)
    LinearLayout mDoorStatus;

    private String mType;
    private String mTitleText;
    private String mTimeText;
    private String mContentText;
    private String mImaggPath;
    private String mImaggType;
    private String mMessageStatusText;
    private String mDoorStatusText;
    private String mInitiatorText;
//    private String mReceiverText;

    private static final int LOAD_IMAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_news);
	ButterKnife.bind(this);
	init();
	event();
    }

    private void init() {
	Bundle bundle = getIntent().getExtras();
	mType = bundle.getString("type");
	mTitleText = bundle.getString("title");
	mTimeText = bundle.getString("time");
	mContentText = bundle.getString("content");
	mImaggPath = bundle.getString("image");
	mMessageStatusText = bundle.getString("message");
	mDoorStatusText = bundle.getString("door");
	mInitiatorText = bundle.getString("initiator");
		mImaggType = bundle.getString("imageType");
//	mReceiverText = bundle.getString("receiver");
    }

    private void event() {
	if (mType.equals("systematic")) {
	    mTitle.setText(R.string.systematic_information);
	} else if (mType.equals("individual")) {
	    mTitle.setText(R.string.individual_information);
	}

	if (mImaggPath != null) {
	    mImage.setVisibility(View.VISIBLE);
//	    String path = mEhomeApplication.getmImageFile().getPath()+"/"+mImaggPath;
//	    Bitmap bm = BitmapFactory.decodeFile(path);  
//	    mImage.setImageBitmap(bm);
		if ("1".equals(mImaggType)) {
			mImage.setImageBitmap(getLoacalBitmap(mImaggPath));
		}else{
	    getImage(mImaggPath);
		}
	}
	if (!mMessageStatusText.equals("-1")) {
	    if (mMessageStatusText.equals("0")) {
		mMessageStatus.setVisibility(View.VISIBLE);
		mMessageStatusShow.setText(R.string.no_connect);
		mMessageStatusShow.setTextColor(getResources().getColor(R.color.red_text));
	    } else if (mMessageStatusText.equals("1")) {
		mMessageStatus.setVisibility(View.VISIBLE);
		mMessageStatusShow.setText(R.string.connect);
		mMessageStatusShow.setTextColor(getResources().getColor(R.color.green));
	    }
	}
	if (!mDoorStatusText.equals("-1")) {
	    if (mDoorStatusText.equals("0")) {
		mDoorStatus.setVisibility(View.VISIBLE);
		mDoorStatusShow.setText(R.string.no_open_door);
		mDoorStatusShow.setTextColor(getResources().getColor(R.color.red_text));
	    } else if (mDoorStatusText.equals("1")) {
		mDoorStatus.setVisibility(View.VISIBLE);
		mDoorStatusShow.setText(R.string.open_door);
		mDoorStatusShow.setTextColor(getResources().getColor(R.color.green));
	    }
	}

	mTitleShow.setText(mTitleText);
	mTime.setText(mTimeText);
	mContent.setText("        "+mContentText);
	mInitiatorShow.setText(mInitiatorText);
	mReceiverShow.setText(mEhomeApplication.getmCurrentUser().getmUsername());

    }

    private Handler mHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case LOAD_IMAGE:
//	    	OkHttp.getImage(mContext, (String) msg.obj, mImage);
		ImageLoader.getInstance().displayImage((String) msg.obj, mImage);
//		OkHttpUtils
//		    .get()//
//		    .url((String) msg.obj)//
//		    .build()//
//		    .execute(new BitmapCallback()
//		    {
//
//		        @Override
//		        public void onResponse(Bitmap bitmap)
//		        {
//		            mImage.setImageBitmap(bitmap);
//		        }
//
//			@Override
//			public void onError(Call call, Exception e) {
//			    // TODO Auto-generated method stub
//			    
//			}
//
//		    });
		LogUtils.i("图片地址"+(String) msg.obj);
		break;

	    default:
		break;
	    }
	}
    };

    private void getImage(String imaggPath) {
    	Map<String, String> params =new HashMap<String, String>();
	params.put("objectKey", imaggPath);
	OkHttp.get(mContext,ConnectPath.MESSAGEPICTURE_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
		@Override
		public void onResponse(JSONObject response) {
			try {
			    if (response.has("obj")) {
				Message msg = new Message();
				msg.what = LOAD_IMAGE;
				msg.obj = response.getString("obj");
				mHandler.sendMessage(msg);

			    }
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
	}));
    }

    @OnClick(R.id.news_back)
    public void onClick(View v) {
	InformationActivity.this.finish();

    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
		ButterKnife.unbind(this);
    }

	public static Bitmap getLoacalBitmap(String url) {

		try {

			FileInputStream fis = new FileInputStream(url);

			return BitmapFactory.decodeStream(fis);

		} catch (FileNotFoundException e) {

			e.printStackTrace();

			return null;

		}

	}

}
