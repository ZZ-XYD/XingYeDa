package com.xingyeda.ehome.door;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.zxing.encode.CodeCreator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareActivity extends BaseActivity {

    @Bind(R.id.share_facility_img)
    ImageView shareImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        String path = getIntent().getExtras().getString("QRCode");

        ViewGroup.LayoutParams para = shareImg.getLayoutParams();
        para.width = mScreenW / 4 * 3;//修改宽度
        para.height = mScreenW / 4 * 3;//修改高度
        shareImg.setLayoutParams(para);

        try {
            Bitmap  bitmap = CodeCreator.createQRCode(path);
            shareImg.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.share_facility_back)
    public void onViewClicked() {
        finish();
    }
}
