package com.xingyeda.ehome.park;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParkMessageActivity extends BaseActivity {

    @Bind(R.id.park_msg_title)
    TextView parkMsgTitle;
    @Bind(R.id.park_msg_title_text)
    TextView parkMsgTitleText;
    @Bind(R.id.park_msg_time)
    TextView parkMsgTime;
    @Bind(R.id.park_msg_content)
    TextView parkMsgContent;
    @Bind(R.id.park_msg_image)
    ImageView parkMsgImage;

    private String titleText;
    private String time;
    private String content;
    private String picture;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_message);
        ButterKnife.bind(this);

        init();
    }
    private void init(){
        Bundle bundle = getIntent().getExtras();
        titleText = bundle.getString("title");
        time = bundle.getString("time");
        content = bundle.getString("content");
        picture = bundle.getString("image");

        parkMsgTitle.setText("车辆出入信息");
        parkMsgTitleText.setText(titleText);
        parkMsgTime.setText(time);
        parkMsgContent.setText(content);

        ImageLoader.getInstance().displayImage(picture, parkMsgImage);
    }

    @OnClick(R.id.park_msg_back)
    public void onViewClicked() {
        finish();
    }
}
