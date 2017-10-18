package com.xingyeda.ehome;

import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mob.MobSDK;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.wifiOnOff.MainActivity;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import static android.R.attr.action;

public class Test extends BaseActivity {
    private PlatformDb platDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        MobSDK.init(mContext,"214357362328d","ac5f9099ffb9fe286b9c8e7d99a99b44");
//        MobSDK.init(mContext);
    }

    @OnClick(R.id.qq)
    public void onViewClicked() {
        Platform  qq= ShareSDK.getPlatform(QQ.NAME);
        authorize(qq);
    }
    /**
     * 执行授权,获取用户信息
     *
     * @param plat
     */
    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }

        // 使用SSO授权。有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
        plat.SSOSetting(false);
//        plat.authorize();
        // 参数null表示获取当前授权用户资料
        plat.showUser(null);
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {//授权成功
                String headImageUrl = null;//头像
                String token;//token
                String gender;//年龄
                String userId;
                String name = null;//用户名

                // 用户资源都保存到res
                // 通过打印res数据看看有哪些数据是你想要的
                if (action == Platform.ACTION_USER_INFOR) {
                    platDB = platform.getDb(); // 获取数平台数据DB
                    if (platform.getName().equals(Wechat.NAME)) {

                        // 通过DB获取各种数据
                        token = platDB.getToken();
                        userId = platDB.getUserId();
                        name = platDB.getUserName();
                        gender = platDB.getUserGender();
                        headImageUrl = platDB.getUserIcon();
                        if ("m".equals(gender)) {
                            gender = "1";
                        } else {
                            gender = "2";
                        }

                    } else if (platform.getName().equals(SinaWeibo.NAME)) {
                        // 微博登录
                    } else if (platform.getName().equals(QQ.NAME)) {
                        // QQ登录
                        token = platDB.getToken();
                        userId = platDB.getUserId();
                        name = hashMap.get("nickname").toString(); // 名字
                        gender = hashMap.get("gender").toString(); // 年龄
                        headImageUrl = hashMap.get("figureurl_qq_2").toString(); // 头像figureurl_qq_2 中等图，figureurl_qq_1缩略图
                        String city = hashMap.get("city").toString(); // 城市
                        String province = hashMap.get("province").toString(); // 省份
//                        getUserInfo(name, headImageUrl);

                    }
                }


            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {//授权失败
                Toast.makeText(mContext, "错误", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }

            @Override
            public void onCancel(Platform platform, int action) {//取消授权

            }
        });
        plat.removeAccount(true);


    }
}
