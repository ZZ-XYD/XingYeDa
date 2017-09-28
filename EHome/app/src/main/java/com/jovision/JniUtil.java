package com.jovision;

import android.app.Application;
import android.util.Log;
import android.view.Surface;

import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.jovision.play.RemoteVideo;
import com.xingyeda.ehome.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jovision.Jni.sendBytes;
import static u.aly.au.as;

/**
 * Created by juyang on 16/3/23.
 */
public class JniUtil {

    private static final String TAG = "JniUtil";
    private static ElianNative elian;// 智联路由
    private static byte[] acFLBuffer = new byte[2048];


    /****************************************     视频连接      ***********************************/

    /**
     * 1.初始化网络库（程序开启后必须调用此方法）
     *
     * @param app     Application
     * @param logPath 日志的路径
     * @param localIp 手机当前外网ip
     * @return
     */
    public static boolean initSDK(Application app, String logPath, String localIp) throws IOException {
        boolean initSdkRes = false;

        try {
            boolean initRes = Jni.init(app, 9200, logPath, localIp);//初始化网络库
            boolean enableHelper = false;
            if (initRes) {
                enableHelper = Jni.enableLinkHelper(true, 3, 10);//开启小助手
                Jni.enableLog(true);//打开底层log打印
            }
            Jni.setStat(true);//显示统计
            Log.v(TAG, "initSDK:initSdkRes=" + initSdkRes + ";initRes=" + initRes + ";enableHelper=" + enableHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return initSdkRes;
    }

    /**
     * 2.退出软件时调用此方法
     *
     * @return
     */
    public static void deinitSDK() {
        Jni.deinit();
    }


    /**
     * 1.正常视频连接，需要显示视频
     *
     * @param channel
     * @param surface
     * @param scenePath
     * @return
     */
    public static int connectDevice(Channel channel, Surface surface,
                                    String scenePath, boolean isApConnect) {
        int result = -1;

        if (null == channel) {
            return result;
        }
        Device device = channel.getParent();
        if (null != device && null != channel) {
            if ("".equalsIgnoreCase(device.getIp()) || 0 == device.getPort()) {// 无ip和端口走云视通连接
                Log.e("connect", "connected By Yst:devNum=" + device.getFullNo()
                        + ";user=" + device.getUser()
                        + ";pwd=" + device.getPwd()
                        + ";group=" + device.getGid()
                        + ";num=" + device.getNo()+ ";channel=" + channel.getChannel());

//
//                result = Jni.connect(channel.getIndex(), channel.getChannel(),
//                        device.getIp(), device.getPort(), device.getUser(),
//                        device.getPwd(), device.getNo(), device.getGid(), true,
//                        JVNetConst.JVN_TRYTURN, true, JVNetConst.TYPE_3GMO_UDP, surface, false,
//                        false, isApConnect, false, scenePath);
                result = Jni.connect(channel.getIndex(),
                        1,
                        channel.getChannel(),
                        device.getIp(),
                        device.getPort(),
                        device.getUser(),
                        device.getPwd(),
                        device.getNo(),
                        device.getGid(),
                        true,
                        JVNetConst.JVN_TRYTURN,
                        true,
                        JVNetConst.TYPE_3GMO_UDP,
                        surface,
                        false,
                        null,
                        isApConnect,
                        false,
                        scenePath,0);
                Log.e("connect", "connected By Yst:devNum=" + device.getFullNo() + ";user="
                        + device.getUser() + ";pwd=" + device.getPwd()
                        + ";group=" + device.getGid()
                        + ";num=" + device.getNo()+ ";channel=" + channel.getChannel());
            } else {// 有Ip用ip连接，云视通号字段需要传-1，否则仍然走的云视通连接
                result = Jni.connect(channel.getIndex(), channel.getChannel(),
                        device.getIp(), device.getPort(), device.getUser(),
                        device.getPwd(), -1, device.getGid(), true, JVNetConst.JVN_TRYTURN,
                        true,
                        JVNetConst.TYPE_3GMO_UDP, surface, false, false,
                        isApConnect, false, scenePath);
                Log.e("connect", "connected By Ip:ip=" + device.getIp() + ";port=" + device.getPort());
            }
        }
        return result;
    }

    /**
     * 2.断开指定窗口视频
     *
     * @param index
     * @return
     */
    public static boolean disconnectChannel(int index) {
        return Jni.disconnect(index);
    }

    /**
     * 只发关键帧
     *
     * @param index
     */
    public static void sendOnlyI(int index) {
        Jni.sendCmd(index, (byte) JVNetConst.JVN_CMD_ONLYI, new byte[0],
                0);
    }

    /**
     * 发全帧
     *
     * @param index
     */
    public static void sendFull(int index) {
        Jni.sendCmd(index, (byte) JVNetConst.JVN_CMD_FULL, new byte[0],
                0);
    }

    /**
     * 暂停底层显示
     *
     * @param index 窗口索引
     * @return true：成功，false：失败
     */
    public static boolean pauseSurface(int index) {
        return Jni.pause(index);
    }

    /**
     * 恢复底层显示
     *
     * @param index   窗口索引
     * @param surface
     * @return true：成功，false：失败
     */
    public static boolean resumeSurface(int index, Object surface) {
        return Jni.resume(index, surface);
    }

    /**
     * 暂停实时视频播放
     *
     * @param index
     */
    public static boolean pauseVideo(int index) {
        pauseSurface(index);
        return sendBytes(index,
                JVNetConst.JVN_CMD_VIDEOPAUSE, new byte[0], 8);
    }


    /**
     * 继续实时视频播放
     *
     * @param index
     */
    public static boolean resumeVideo(int index, Surface surface) {
        boolean resume = resumeSurface(index, surface);
        return sendBytes(index,
                JVNetConst.JVN_CMD_VIDEO, new byte[0], 8);
    }

    /****************************************     文本聊天相关      ***********************************/

    /**
     * 请求文本聊天（文本聊天按通道发请求）
     *
     * @param index
     * @return
     */
    public static boolean requestTextChat(int index) {
        return sendBytes(index, JVNetConst.JVN_REQ_TEXT, new byte[0], 8);
    }

    /**
     * 获取主控码流信息请求（文本聊天按通道发请求）
     *
     * @param index
     * @return
     */
    public static boolean requestStreamData(int index) {
        return Jni.sendTextData(index, JVNetConst.JVN_RSP_TEXTDATA, 8,
                JVNetConst.JVN_STREAM_INFO);
    }


    /****************************************     音频监听      ***********************************/


    /**
     * 查询音频监听状态
     */
    public static boolean isPlayAudio(int index) {
        return Jni.isPlayAudio(index);
    }

    /**
     * 开始音频监听
     */
    public static boolean startAudioMonitor(int index) {
        resetAecDenoise(index, false, true);
        resumeAudio(index);
        return Jni.enablePlayAudio(index, true);
    }

    /**
     * 停止音频监听
     */
    public static boolean stopAudioMonitor(int index) {
        pauseAudio(index);
        // enableSoundData(index, AppConsts.SWITCH_CLOSE);
        return Jni.enablePlayAudio(index, false);
    }

    /**
     * 设置aec和降噪启用与否
     */
    public static void resetAecDenoise(int index, boolean aec, boolean denoise) {
        Jni.resetAecDenoise(index, aec, denoise);
    }

    /**
     * 恢复音频
     */
    public static boolean resumeAudio(int index) {
        return Jni.resumeAudio(index);
    }

    /**
     * 暂停音频
     */
    public static boolean pauseAudio(int index) {
        return Jni.pauseAudio(index);
    }

    /****************************************     对讲      ***********************************/

    /**
     * 开始语音对讲
     *
     * @param index
     * @param singleVoiceCall true 单向对讲 false 双向对讲
     * @return
     */
    public static boolean startVoiceCall(int index, boolean singleVoiceCall) {
        resetAecDenoise(index, singleVoiceCall ? false : true, true);
        return sendBytes(index, JVNetConst.JVN_REQ_CHAT, new byte[0], 8);
    }

    /**
     * 停止语音对讲
     */
    public static boolean stopVoiceCall(int index) {
        return sendBytes(index, JVNetConst.JVN_CMD_CHATSTOP, new byte[0], 8);
    }

    /**
     * 开始：录本地录音并发送
     */
    public static boolean startRecordSendAudio(int index) {
        return Jni.recordAndsendAudioData(index);
    }


    /****************************************     录像      ***********************************/

    /**
     * 停止：录本地录音并发送
     */
    public static boolean stopRecordSendAudio(int index) {
        return Jni.stopRecordAudioData(index);
    }

    /**
     * 检查窗口是否正在录像
     *
     * @param index
     */
    public static boolean checkRecord(int index) {
        return Jni.checkRecord(index);
    }

    /**
     * 开始录像
     */
    public static boolean startRecord(int index,boolean isTwoRecord) {
        String fileName = System.currentTimeMillis()
                + AppConsts.VIDEO_MP4_KIND;
        boolean recordRes = Jni.startRecord(index, AppConsts.VIDEO_PATH + fileName, true, true, 0,isTwoRecord);
        return recordRes;
    }


    /**
     * 停止录像
     */
    public static boolean stopRecord(int index) {
        return Jni.stopRecord();
    }


    /****************************************     抓拍      ***********************************/
    /**
     * 抓拍
     *
     * @param index
     */
    public static boolean capture(int index) {
        String fileName = System.currentTimeMillis()
                + AppConsts.IMAGE_JPG_KIND;

        boolean res = Jni.screenshot(index, AppConsts.CAPTURE_PATH + fileName, 100);
        return res;
    }


    /****************************************     切换码流      ***********************************/

    /**
     * 切换码流（依赖文本聊天）
     *
     * @param index
     * @param stream 1:高清   2:标清   3:流畅
     */
    public static boolean changeStream(int index, int stream) {
        return Jni.sendString(index,
                JVNetConst.JVN_RSP_TEXTDATA, false, 0,
                AppConsts.TYPE_SET_PARAM, String.format(AppConsts.FORMATTER_CHANGE_STREAM, stream));
    }


    /****************************************     云台      ***********************************/

    /**
     * 云台自动巡航命令
     *
     * @param index
     * @param cmd
     * @param stop
     * @param speed 3-255
     */
    public static void sendCtrlCMDAuto(final int index, final int cmd,
                                       final boolean stop, final int speed) {

        new Thread() {
            @Override
            public void run() {
                byte[] data = new byte[4];
                data[0] = (byte) cmd;
                data[1] = (byte) 0;
                data[2] = (byte) 0;
                data[3] = (byte) speed;
                sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data, 4);
                if (cmd == JVNetConst.JVN_YTCTRL_A)
                    return;

                byte[] data1 = new byte[4];
                data1[0] = (byte) (cmd + 20);
                data1[1] = (byte) 0;
                data1[2] = (byte) 0;
                data1[3] = (byte) speed;
                sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data1, 4);
            }
        }.start();

    }


    /**
     * 长按给云台发命令
     *
     * @param index
     * @param cmd
     * @param stop
     * @param speed 3-255
     */
    public static void sendCtrlCMDLongPush(final int index, final int cmd,
                                           final boolean stop, final int speed) {
        new Thread() {
            @Override
            public void run() {
                byte[] data = new byte[4];
                data[0] = (byte) cmd;
                data[1] = (byte) 0;
                data[2] = (byte) 0;
                data[3] = (byte) speed;
                // 云台命令
                sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data, 4);
                if (stop)
                    return;
                // 如果不是自动命令 发完云台命令接着发一条停止
                byte[] data1 = new byte[4];
                data1[0] = (byte) (cmd + 20);
                data1[1] = (byte) 0;
                data1[2] = (byte) 0;
                data1[3] = (byte) speed;
                sendBytes(index, (byte) JVNetConst.JVN_CMD_YTCTRL, data1, 4);
            }
        }.start();

    }


    /****************************************     广播      ***********************************/


    /**
     * 1.开启广播
     *
     * @param loacalIp
     * @return
     */
    public static int startSearchLan(String loacalIp) {
        Log.v(TAG, "startSearchLan-开启广播");
        return Jni.searchLanServer(9400, 6666, loacalIp);
    }

    /**
     * 2.停止广播
     */
    public static void stopSearchLan() {
        Log.v(TAG, "stopSearchLan-停止广播");
        Jni.stopSearchLanServer();
    }


    /**
     * 3.全网段广播 广播回调：AppConsts.CALL_LAN_SEARCH
     */
    public static void searchAllLanDev() {
        Jni.searchLanDevice("", 0, 0, 0, "", 2000, 2);
        Log.v(TAG, "searchAllLanDev-全网段广播");
    }

    /**
     * 4.纯局域网广播 广播回调：AppConsts.CALL_LAN_SEARCH
     */
    public static void searchLanDev() {
        Jni.queryDevice("A", 361, 2000);
        Log.v(TAG, "searchLanDev-纯局域网广播");
    }

    /****************************************     声波智联      ***********************************/

    /**
     * 1.发送声波(新)
     *
     * @param params 格式:wifiName;wifiPass
     * @param times  播放完成的回调是 AppConsts.CALL_GEN_VOICE
     */
    public static void newSendSoundWave(String params, int times) {
        Jni.genSoundConfig(params, times);
        Log.v(TAG, "new sendSoundWave:params=" + params);
    }

    /**
     * 1.1发送声波(老)
     *
     * @param params 格式:wifiName;wifiPass
     * @param times  播放完成的回调是 AppConsts.CALL_GEN_VOICE
     */
    public static void oldSendSoundWave(String params, int times) {
        Jni.genVoice(params, times);
        Log.v(TAG, "old sendSoundWave:params=" + params);
    }

    /**
     * 2.初始化智联路由
     */
    public static boolean initElian() {
        boolean result = false;
        if (!"x86".equalsIgnoreCase(android.os.Build.CPU_ABI)) {
            // 智联路由
            result = ElianNative.LoadLib();
            if (!result) {
                Log.e(TAG, "initElian:can't load elianjni lib");
            }
            elian = new ElianNative();
        }
        return result;
    }

    /**
     * 3.发送智联路由命令
     *
     * @param wifiName
     * @param wifiPass
     * @param authMode
     */
    public static void sendElian(String wifiName, String wifiPass, byte authMode) {
        if (!"x86".equalsIgnoreCase(android.os.Build.CPU_ABI)) {
            Log.v(TAG, "start zhilian...StartSmartConnection");
            elian.InitSmartConnection(null, 1, 0);// V1
            elian.StartSmartConnection(wifiName, wifiPass,
                    "android smart custom", authMode);
        }
    }


    /**
     * 4.停止智联路由命令
     */
    public static void stopElian() {
        if (!"x86".equalsIgnoreCase(android.os.Build.CPU_ABI) && null !=
                elian) {
            int res = elian.StopSmartConnection();
            Log.v(TAG, "stop zhilian is open elianState=" + res);
        } else {
            Log.v(TAG, "stop zhilian is already closed");
        }
    }

    /***************** 以下为远程回放所有功能 ***************************/

    /**
     * 播放远程文件
     *
     * @param index
     */
    public static void playRemoteFile(int index, String acBuffStr) {
        Log.v(TAG, "acBuffStr=" + acBuffStr);
        sendBytes(index,
                JVNetConst.JVN_REQ_PLAY,
                acBuffStr.getBytes(),
                acBuffStr.getBytes().length);
    }

    /**
     * 停止远程回放视频
     *
     * @param index
     */
    public static boolean stopRemoteFile(int index) {
        Jni.setColor(index, 0, 0, 0, 0);
        return sendBytes(index, JVNetConst.JVN_CMD_PLAYSTOP, new byte[0],
                0);
    }

    /**
     * 暂停远程播放
     *
     * @param index
     */
    public static boolean pausePlay(int index) {
        // 暂停视频
        return sendBytes(index, JVNetConst.JVN_CMD_PLAYPAUSE,
                new byte[0], 0);
    }

    /**
     * 继续远程播放
     *
     * @param index
     */
    public static boolean goonPlay(int index) {
        // 继续播放视频
        return sendBytes(index, JVNetConst.JVN_CMD_PLAYGOON,
                new byte[0], 0);
    }

    /**
     * 是否启用远程回放
     *
     * @param index
     * @param enable
     * @return
     */
    public static boolean enableRemotePlay(int index, boolean enable) {
        boolean enableRes = Jni.enablePlayback(index, enable);
        Log.e("enableRemotePlay", "enable=" + enable + ";enableRes=" + enableRes);
        return enableRes;
    }

    /**
     * 远程回放调节播放进度
     *
     * @param index
     * @param seekProgress
     */
    public static void seekTo(int index, int seekProgress) {
        Jni.sendInteger(index, JVNetConst.JVN_CMD_PLAYSEEK,
                seekProgress);
    }

    /**
     * 远程回放开始下载
     *
     * @param index
     * @param dataByte
     * @param downFileFullName
     */
    public static void startRemoteDownload(int index, byte[] dataByte, String downFileFullName) {
        // 下载之前必须先调用此方法设置文件名
        Jni.setDownloadFileName(downFileFullName);
        sendBytes(index,
                JVNetConst.JVN_CMD_DOWNLOADSTOP,
                new byte[0], 8);
        sendBytes(index,
                JVNetConst.JVN_REQ_DOWNLOAD, dataByte,
                dataByte.length);
    }


    public static void remoteDownload(int index, String dataByte, String downFileFullName) {
        // 下载之前必须先调用此方法设置文件名
        Jni.setDownloadFileName(downFileFullName);
        boolean b = Jni.sendBytes(index,
                JVNetConst.JVN_REQ_DOWNLOAD, dataByte.getBytes(),
                dataByte.getBytes().length);
        LogUtils.i("猫眼图片下载 ： "+b);
    }

    /**
     * 远程回放取消下载
     *
     * @param index
     */
    public static void cancelRemoteDownload(int index) {
        sendBytes(index,
                JVNetConst.JVN_CMD_DOWNLOADSTOP,
                new byte[0], 8);
        Jni.cancelDownload();
    }

    /**
     * 查询远程回放数据方法
     *
     * @param index
     * @param date  数据从回调中返回
     */
    public static void checkRemoteData(int index, String date) {
        sendBytes(index, (byte) JVNetConst.JVN_REQ_CHECK, date.getBytes(),
                date.length());
    }

    /**
     * 远程检索回调获取到码流数据list
     *
     * @param pBuffer
     * @param deviceType
     * @param channelOfChannel
     * @return
     */
    public static ArrayList<RemoteVideo> getRemoteList(byte[] pBuffer,
                                                       int deviceType, int channelOfChannel) {

        ArrayList<RemoteVideo> datalist = new ArrayList<RemoteVideo>();

        try {
            String textString1 = new String(pBuffer);
            Log.v("远程回放pBuffer", "deviceType=" + deviceType + ";pBuffer="
                    + textString1);

            int nSize = pBuffer.length;
            // 无数据
            if (nSize == 0) {
                return datalist;
            }

            if (deviceType == 0) {
                for (int i = 0; i <= nSize - 7; i += 7) {
                    RemoteVideo rv = new RemoteVideo();
                    rv.remoteChannel = String.format("%02d", channelOfChannel);
                    rv.remoteDate = String.format("%c%c:%c%c:%c%c",
                            pBuffer[i + 1], pBuffer[i + 2], pBuffer[i + 3],
                            pBuffer[i + 4], pBuffer[i + 5], pBuffer[i + 6]);
                    rv.remoteDisk = String.format("%c", pBuffer[i]);
                    datalist.add(rv);
                }
            } else if (deviceType == 1 || deviceType == 4 || deviceType == 5) {
                int nIndex = 0;
                for (int i = 0; i <= nSize - 10; i += 10) {
                    acFLBuffer[nIndex++] = pBuffer[i];// 录像所在盘
                    acFLBuffer[nIndex++] = pBuffer[i + 7];// 录像类型
                    RemoteVideo rv = new RemoteVideo();
                    rv.remoteChannel = String.format("%c%c", pBuffer[i + 8],
                            pBuffer[i + 9]);
                    rv.remoteDate = String.format("%c%c:%c%c:%c%c",
                            pBuffer[i + 1], pBuffer[i + 2], pBuffer[i + 3],
                            pBuffer[i + 4], pBuffer[i + 5], pBuffer[i + 6]);
                    rv.remoteKind = String.format("%c", pBuffer[i + 7]);
                    rv.remoteDisk = String.format("%s%d", "",
                            (pBuffer[i] - 'C') / 10 + 1);
                    datalist.add(rv);
                }

            } else if (deviceType == 2 || deviceType == 3) {
                for (int i = 0; i <= nSize - 7; i += 7) {
                    RemoteVideo rv = new RemoteVideo();
                    rv.remoteChannel = String.format("%02d", channelOfChannel);
                    rv.remoteDate = String.format("%c%c:%c%c:%c%c",
                            pBuffer[i + 1], pBuffer[i + 2], pBuffer[i + 3],
                            pBuffer[i + 4], pBuffer[i + 5], pBuffer[i + 6]);
                    rv.remoteDisk = String.format("%c", pBuffer[i]);
                    datalist.add(rv);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return datalist;
    }

    /**
     * 拼接远程回放视频参数
     *
     * @param videoBean
     * @param isJFH
     * @param deviceType
     * @param year
     * @param month
     * @param day
     * @param listIndex
     * @return
     */
    public static String getPlayFileString(RemoteVideo videoBean,
                                           boolean isJFH, int deviceType, int year, int month, int day,
                                           int listIndex) {
        byte acChn[] = new byte[3];
        byte acTime[] = new byte[10];
        byte acDisk[] = new byte[2];
        String acBuffStr = "";
        if (null == videoBean) {
            return acBuffStr;
        }

        Log.v("远程回放单个文件", "deviceType=" + deviceType + ";isJFH=" + isJFH);
        if (isJFH) {
            if (deviceType == 0) {

                // sprintf(acChn, "%s",videoBean.remoteChannel);
                String channelStr = String
                        .format("%s", videoBean.remoteChannel);
                Log.e("channelStr", channelStr);
                System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
                        channelStr.length());

                // sprintf(acTime, "%s",videoBean.remoteDate);
                String acTimeStr = String.format("%s", videoBean.remoteDate);
                System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
                        acTimeStr.length());

                // sprintf(acDisk, "%s",videoBean.remoteDisk);
                String acDiskStr = String.format("%s", videoBean.remoteDisk);
                System.arraycopy(acDiskStr.getBytes(), 0, acDisk, 0,
                        acDiskStr.length());
                acBuffStr = String.format(
                        "%c:\\JdvrFile\\%04d%02d%02d\\%c%c%c%c%c%c%c%c.mp4",
                        acDisk[0], year, month, day, acChn[0], acChn[1],
                        acTime[0], acTime[1], acTime[3], acTime[4], acTime[6],
                        acTime[7]);

            } else if (deviceType == 1 || deviceType == 4 || deviceType == 5) {
                String channelStr = String
                        .format("%s", videoBean.remoteChannel);
                System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
                        channelStr.length());

                // sprintf(acTime, "%s",videoBean.remoteDate);
                String acTimeStr = String.format("%s", videoBean.remoteDate);
                System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
                        acTimeStr.length());

//                acBuffStr = String.format(
//                        "./rec/%02d/%04d%02d%02d/%c%c%c%c%c%c%c%c%c.mp4",
//                        acFLBuffer[listIndex * 2] - 'C', year, month, day,
//                        acFLBuffer[listIndex * 2 + 1], acChn[0], acChn[1],
//                        acTime[0], acTime[1], acTime[3], acTime[4], acTime[6],
//                        acTime[7]);


                acBuffStr = String.format(
                        "./rec/%02d/%04d%02d%02d/%c%c%c%c%c%c%c%c%c.mp4",
                        acFLBuffer[listIndex * 2] - 'C', year, month, day,
                        acFLBuffer[listIndex * 2 + 1], acChn[0], acChn[1],
                        acTime[0], acTime[1], acTime[3], acTime[4], acTime[6],
                        acTime[7]);

            }

            Log.v("url: ", acBuffStr);
        } else if (deviceType == 0) {
            String channelStr = String.format("%s", videoBean.remoteChannel);
            System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
                    channelStr.length());

            // sprintf(acTime, "%s",videoBean.remoteDate);
            String acTimeStr = String.format("%s", videoBean.remoteDate);
            System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
                    acTimeStr.length());

            // sprintf(acDisk, "%s",videoBean.remoteDisk);
            String acDiskStr = String.format("%s", videoBean.remoteDisk);
            System.arraycopy(acDiskStr.getBytes(), 0, acDisk, 0,
                    acDiskStr.length());

            acBuffStr = String.format(
                    "%c:\\JdvrFile\\%04d%02d%02d\\%c%c%c%c%c%c%c%c.sv4",
                    acDisk[0], year, month, day, acChn[0], acChn[1], acTime[0],
                    acTime[1], acTime[3], acTime[4], acTime[6], acTime[7]);

        } else if (deviceType == 1 || deviceType == 4 || deviceType == 5) {
            String channelStr = String.format("%s", videoBean.remoteChannel);
            System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
                    channelStr.length());
            Log.v("channelStr:", channelStr);
            // sprintf(acTime, "%s",videoBean.remoteDate);
            String acTimeStr = String.format("%s", videoBean.remoteDate);
            Log.v("acTimeStr:", acTimeStr);
            System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
                    acTimeStr.length());
            acBuffStr = String.format(
                    "./rec/%02d/%04d%02d%02d/%c%c%c%c%c%c%c%c%c.sv5",
                    acFLBuffer[listIndex * 2] - 'C', year, month, day,
                    acFLBuffer[listIndex * 2 + 1], acChn[0], acChn[1],
                    acTime[0], acTime[1], acTime[3], acTime[4], acTime[6],
                    acTime[7]);
            Log.v("acBuffStr:", acBuffStr);
        } else if (deviceType == 2 || deviceType == 3) {
            String channelStr = String.format("%s", videoBean.remoteChannel);
            System.arraycopy(channelStr.getBytes(), 0, acChn, 0,
                    channelStr.length());

            // sprintf(acTime, "%s",videoBean.remoteDate);
            String acTimeStr = String.format("%s", videoBean.remoteDate);
            System.arraycopy(acTimeStr.getBytes(), 0, acTime, 0,
                    acTimeStr.length());

            // sprintf(acDisk, "%s",videoBean.remoteDisk);
            String acDiskStr = String.format("%s", videoBean.remoteDisk);
            System.arraycopy(acDiskStr.getBytes(), 0, acDisk, 0,
                    acDiskStr.length());
            acBuffStr = String.format(
                    "%c:\\JdvrFile\\%04d%02d%02d\\%c%c%c%c%c%c%c%c.sv6",
                    acDisk[0], year, month, day, acChn[0], acChn[1], acTime[0],
                    acTime[1], acTime[3], acTime[4], acTime[6], acTime[7]);
            Log.v("url: ", acBuffStr);

        }
        Log.v("tags", "bytesize: " + acBuffStr.getBytes().length + ", url:"
                + acBuffStr);
        acChn = null;
        acTime = null;
        acDisk = null;

        return acBuffStr;
    }


    /****************************************     其他      ***********************************/

    /**
     * 递归创建文件目录
     *
     * @param filePath 要创建的目录路径
     * @author
     */
    public static void createDirectory(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return;
        }
        File parentFile = file.getParentFile();

        if (null != file && parentFile.exists()) {
            if (parentFile.isDirectory()) {
            } else {
                parentFile.delete();
                boolean res = parentFile.mkdir();
                if (!res) {
                    parentFile.delete();
                }
            }

            boolean res = file.mkdir();
            if (!res) {
                file.delete();
            }

        } else {
            createDirectory(file.getParentFile().getAbsolutePath());
            boolean res = file.mkdir();
            if (!res) {
                file.delete();
            }
        }
    }


    /**
     * 特定 json 转 HashMap
     *
     * @param msg
     * @return
     */
    public static HashMap<String, String> genMsgMap(String msg) {
        HashMap<String, String> map = new HashMap<String, String>();

        if (null == msg || "".equalsIgnoreCase(msg)) {
            return null;
        }
        Matcher matcher = Pattern.compile("([^=;]+)=([^=;]+)").matcher(msg);
        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }

    /**
     * 特定 json 转 HashMap 不会覆盖
     *
     * @param msg
     * @return
     */
    public static HashMap<String, String> genMsgMap1(String msg) {
        HashMap<String, String> map = new HashMap<String, String>();

        if (null == msg || "".equalsIgnoreCase(msg)) {
            return null;
        }
        Matcher matcher = Pattern.compile("([^=;]+)=([^=;]+)").matcher(msg);
        while (matcher.find()) {
            if (null != map.get(matcher.group(1))
                    && !"".equalsIgnoreCase(matcher.group(1))) {

            } else {
                map.put(matcher.group(1), matcher.group(2));
            }

        }
        return map;
    }

    /**
     * 1.报警推送开关
     *
     * @param index
     * @param switchState 通用开关
     * @return 回调：码流数据里面返回bAlarmEnable
     */
    public static boolean setDevSafeState(int index, int switchState) {
        return Jni.sendString(index, JVNetConst.JVN_RSP_TEXTDATA,
                true, 0x07, 0x02, String.format(Locale.CHINA,
                        AppConsts.FORMATTER_SET_DEV_SAFE_STATE, switchState));
    }

    /**
     * 2.设置移动侦测开关
     *
     * @param index
     * @param switchState 通用开关
     * @return 回调：码流数据里面返回bMDEnable
     */
    public static boolean setMotionDetection(int index, int switchState) {
        return Jni.sendString(index, JVNetConst.JVN_RSP_TEXTDATA,
                true, 0x06, 0x02,
                String.format(Locale.CHINA, AppConsts.FORMATTER_SET_MDENABLE_STATE, switchState));
    }


    /**
     * 3.设置移动侦测灵敏度
     *
     * @param index
     * @param sensity param = "nMDSensitivity=10"; FORMATTER_NMDSENSITIVITY
     * @return 回调：JVNetConst.JVN_MOTION_DETECT_GET_CALLBACK 17
     */

    public static boolean setMDSensitivity(int index, int sensity) {
        String param = String.format(Locale.CHINA, AppConsts.FORMATTER_NMDSENSITIVITY, sensity);
        return Jni.sendString(index,
                JVNetConst.JVN_RSP_TEXTDATA, true,
                JVNetConst.RC_EX_MDRGN, JVNetConst.EX_MDRGN_SUBMIT,
                param);
    }
}
