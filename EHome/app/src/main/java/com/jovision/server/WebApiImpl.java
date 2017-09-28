package com.jovision.server;


import com.jovision.server.listener.ResponseListener;
import com.jovision.server.presenter.IWebApiPresenter;
import com.jovision.server.presenter.WebApiPresenterImpl;

import java.util.HashMap;


/**
 * Web服务端接口实现
 *
 * @author ye.jian
 */

public class WebApiImpl {

    // ---------------------------------------------
    // #
    // ---------------------------------------------
    private WebApiImpl() {
    }

    private static class SingletonLoader {
        private static final WebApiImpl INSTANCE = new
                WebApiImpl();
    }

    public static WebApiImpl getInstance() {
        return SingletonLoader.INSTANCE;
    }

    private IWebApiPresenter getWebApiPresenter() {
        return new WebApiPresenterImpl();
    }
    // ################Web服务端接口↓###############
    // -------------------------------------------
    // # 账号管理
    // -------------------------------------------

    /**
     * 注册操作
     *
     * @param <T>
     * @param account      用户名
     * @param password     密码
     * @param validateCode 验证码
     * @WebInterface
     */
    public <T> void register(final String account, final String password,
                             final String validateCode, final
                             ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        params.put("validateCode", validateCode);
        getWebApiPresenter().request("/accountManage/accountregister.do",
                params,
                listener);
    }

    /**
     * 注册操作 TODO 不需要验证码的注册
     *
     * @date 2017/05/12新增
     *
     * @param <T>
     * @param account      用户名
     * @param password     密码
     */
    public <T> void registerWithoutValidateCode(final String account, final String password,
                                                final String sig, final
                             ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        params.put("sig", sig);
        getWebApiPresenter().request("/accountManage/accountreg4Third.do",
                params,
                listener);
    }

    /**
     * 用户是否存在
     *
     * @param <T>
     * @param account 邮箱/手机号
     * @return void
     * @WebInterface
     */
    public <T> void isAccountExist(final String account,
                                   final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("account", account);
        getWebApiPresenter().request("/accountManage/accountisExist.do", params,
                listener);
    }

    /**
     * 为某个帐号发送验证码
     *
     * @param <T>
     * @param account
     * @WebInterface
     */
    public <T> void validateCode(final String account,
                                 final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("account", account);
        getWebApiPresenter().request
                ("/accountManage/accountsendValidateCode4APP" +
                        ".do", params, listener);
    }

    /**
     * 修改密码
     *
     * @param <T>
     * @param oldPwd       原密码
     * @param newPwd       新密码
     * @param confirmPwd   新密码确认
     * @param loginAccount 当前登录的账号(手机/邮箱/用户名)
     * @return return jsonstring
     * @WebInterface
     */
    public <T> void modifyPwd(final String oldPwd, final String newPwd,
                              final String confirmPwd, final String
                                      loginAccount, final ResponseListener<T>
                                      listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("oldPwd", oldPwd);
        params.put("newPwd", newPwd);
        params.put("confirmPwd", confirmPwd);
        params.put("accinfo", loginAccount);
        getWebApiPresenter().request("/accountManage/accountmodifyPwd.do",
                params, listener);
    }

    /**
     * 找回密码
     *
     * @param <T>
     * @param account      手机号/邮箱
     * @param newPwd       新密码
     * @param confirmPwd   新密码确认
     * @param validateCode 验证码
     * @return return jsonstring
     * @WebInterface
     */
    public <T> void forgetPwd(final String account, final String newPwd,
                              final String confirmPwd, final String
                                      validateCode,
                              final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("account", account);
        params.put("newPwd", newPwd);
        params.put("confirmPwd", confirmPwd);
        params.put("validateCode", validateCode);
        getWebApiPresenter().request("/accountManage/accountforgetPwd.do",
                params, listener);
    }

    // -------------------------------------------
    // # 设备管理
    // -------------------------------------------

    /**
     * 获取设备列表操作
     *
     * @param <T>
     */
    public <T> void getDevices(final ResponseListener<T> listener) {
        getWebApiPresenter().request("/deviceManage/deviceListData.do", null,
                listener);
    }

    /**
     * 获取某个设备的信息
     *
     * @param listener
     * @param gid      设备的云视通号
     */
    public <T> void getDeviceInfoNew(final String gid, final ResponseListener<T> listener) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", gid);
        getWebApiPresenter().request("/deviceManage/deviceInfoNew.do", params,
                listener);
    }

    /**
     * 添加一个设备
     *
     * @param <T>
     * @param guid     云视通号
     * @param username 流媒体账户名
     * @param password 视频登录密码
     * @param nickname 设备昵称
     * @return
     */
    public <T> void addDevice(final String guid, final String username,
                              final String password, final String nickname,
                              final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", guid);
        params.put("deviceUsername", username);
        params.put("devicePassword", password);
        params.put("deviceName", nickname);
        getWebApiPresenter().request("/deviceManage/deviceuserBind.do", params,
                listener);
    }

    /**
     * 删除设备
     *
     * @param <T>
     * @param guid 云视通号
     * @return
     */
    public <T> void deleteDevice(final String guid,
                                 final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", guid);
        getWebApiPresenter().request("/deviceManage/devicereleaseBind.do",
                params,
                listener);
    }

    /**
     * 分享我的设备给其他帐号
     *
     * @param <T>
     * @param ids       被分享者用户Id
     * @param deviceGid
     */
    public <T> void shareDevice(final ResponseListener<T> listener,
                                final String deviceGid, final String... ids) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userIdArr", ids);
        params.put("deviceGuid", deviceGid);
        getWebApiPresenter().request("/deviceManage/deviceuserShare.do", params,
                listener);
    }

    /**
     * 取消分享
     *
     * @param <T>
     * @param
     * @param deviceGid
     * @return
     */
    public <T> void cancelShare(final String deviceGid,
                                final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", deviceGid);
        getWebApiPresenter().request("/deviceManage/deviceshareeRelease.do",
                params,
                listener);
    }

    /**
     * 删除得到的分享设备
     *
     * @param <T>
     * @param username
     * @param deviceGid
     * @return
     */
    public <T> void deleteShare(final String username, final String deviceGid,
                                final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userIdArr", username);
        params.put("deviceGuid", deviceGid);
        getWebApiPresenter().request("/deviceManage/deviceMultiDelShare.do",
                params,
                listener);
    }

    /**
     * 修改设备信息
     *
     * @param <T>
     * @param guid     设备云视通号
     * @param username 新的设备用户名
     * @param password 新的设备密码
     * @return
     */
    public <T> void modifyDevice(final String guid, final String username,
                                 final String password, final
                                 ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", guid);
        params.put("deviceUsername", username);
        params.put("devicePassword", password);
        getWebApiPresenter().request("/deviceManage/deviceModify.do", params,
                listener);
    }

    /**
     * 修改设备昵称
     *
     * @param guid     设备云视通号
     * @param nickName 新昵称
     */
    public <T> void modifyDeviceNickName(final String guid, final String
            nickName,
                                         final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", guid);
        params.put("deviceName", nickName);
        getWebApiPresenter().request("/deviceManage/deviceModifyNickname.do",
                params,
                listener);
    }

    /**
     * 获取设备分享列表
     *
     * @param <T>
     * @param guid 设备的云视通号
     * @return
     */
    public <T> void getShareList(final String guid,
                                 final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", guid);
        getWebApiPresenter().request("/deviceManage/deviceSharedUsers.do",
                params,
                listener);
    }

    /**
     * 获取设备分享的二维码
     *
     * @param <T>
     * @param guid 设备的云视通号
     * @return
     */
    public <T> void getShareQr(final String guid, final String nickName,
                               final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", guid);
        params.put("deviceNickname", nickName);
        getWebApiPresenter().request("/deviceManage/deviceGenQRCode.do",
                params,
                listener);
    }

    /**
     * 通过扫描二维码添加分享的设备
     *
     * @param <T>
     * @param guid 设备的云视通号
     * @return
     */
    public <T> void addShareDevByQr(final String guid, final String nickName,
                                    final String token,
                                    final ResponseListener<T> listener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceGuid", guid);
        params.put("deviceNickname", nickName);
        params.put("token", token);
        getWebApiPresenter().request("/deviceManage/deviceShareAdd.do",
                params,
                listener);
    }
}
