package com.jovision;

import android.util.Log;
import android.view.Surface;


import java.io.File;
import java.util.ArrayList;


/**
 * Created by Administrator on 2016/11/14.
 */

public class PlayUtil {

    private static final String TAG = "playUtils";

    public static void deinitSDK() {
        Jni.deinit();
    }

    /**
     * 1.查询到的猫眼对应的服务器地址
     * @param savePath
     * @param ystNum
     * @return
     */
    public static int searchStreamCatOnLineServer(String savePath,String[] ystNum){
        return Jni.strMedAddress(savePath, ystNum.length, ystNum);
    }

    /**
     * 2.猫眼上线
     * @param ystNum
     * @param savePath
     * @return
     */
    public static int streamCatOnLine(String ystNum,String savePath){
        return Jni.strMedOnline(ystNum, savePath);
    }

    /**
     * 3.猫眼视频连接
     * @param window
     * @param surface
     * @param ystNum
     * @return
     */
//    public static int streamCatConnect(int window, Surface surface, String ystNum){
//        return Jni.strMedConnect(window, surface, ystNum, 1,"");
//    }
    /**
     * 3.猫眼视频连接
     * @param window
     * @param surface
     * @param ystNum
     * @return
     */
    public static int streamCatConnect(int window, Surface surface, String ystNum){
        return Jni.strMedConnect(window, surface, ystNum, 1,"",false,"","");
    }
    public static int streamCatConnect2(int window, Surface surface, String ystNum){
        return Jni.strMedConnect(window, surface, ystNum, 0,"",false,"","");
    }


    /**
     * 4.猫眼视频连接断开
     * @param window
     */
    public static void streamCatDisconnect(int window){
        Jni.strMedDisconnect(window);
    }


    /**
     * 5.猫眼设置接口
     * @param window
     * @param cmd
     * @param param
     * @param nPacketType
     * @param data
     * @param size
     * @return
     */
    public static int streamCatSendData(int window,int cmd,int param,byte nPacketType,byte[] data,int size){
        int sendRes = Jni.strMedSendData(window,cmd,param,nPacketType,data,size);
        Log.e(TAG,"streamCatSendData_res="+sendRes+";window="+window);
        return sendRes;
    }


    /**
     * 6.开启监听
     * @param window
     */
    public static boolean openSound(int window){
        Jni.resetAecDenoise(window, false, true);
        resumeAudio(window);
        return Jni.enablePlayAudio(window,true);
    }

    /**
     * 7.关闭监听
     * @param window
     */
    public static boolean closeSound(int window){
        pauseAudio(window);
        return Jni.enablePlayAudio(window,false);
    }

    /**
     * 8.视频抓拍
     * @param window
     * @param fileName
     * @return
     */
    public static boolean capture(int window,String fileName){
        return Jni.screenshot(window,fileName,100);
    }

    /**
     * 9.开启录像
     * @param window
     * @param fileName
     */
    public static boolean startRecord(int window,String fileName){
        return Jni.startRecord(window,fileName,true,true,0);
    }

    /**
     * 10.停止录像
     */
    public static boolean stopRecord(){
        return Jni.stopRecord();
    }

    /**
     * 11.开启对讲
     * @param window
     * @return
     */
    public static int startStreamVoiceCall(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_CHAT,
                0, JVNetConst.SRC_EX_START_CHAT, null, 0);
        Log.e("CATCAT", "startStreamVoiceCall_res=" + sendRes + ";window=" + window);
        return sendRes;
    }

    /**
     * 12.关启对讲
     * @param window
     * @return
     */
    public static int stopStreamVoiceCall(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_CHAT,
                0, JVNetConst.SRC_EX_STOP_CHAT, null, 0);
        Log.e("CATCAT", "stopStreamVoiceCall_res=" + sendRes + ";window=" + window);
        return sendRes;
    }

    /**
     * 13.开始：录本地录音并发送
     */
    public static void startRecordSendAudio(int index) {
        Jni.recordAndsendAudioData(index, 1);
        Log.e("CATCAT", "sendsound_startRecordSendAudio");
    }

    /**
     * 14.停止：录本地录音并发送
     */
    public static boolean stopRecordSendAudio(int index) {
        return Jni.stopRecordAudioData(index);
    }

    /**
     * 15.恢复音频播放
     */
    public static boolean resumeAudio(int index) {
        return Jni.resumeAudio(index);
    }

    /**
     * 16.停止音频播放
     */
    public static boolean pauseAudio(int index) {
        return Jni.pauseAudio(index);
    }

    /**
     * 17.猫眼流媒体切换码流
     *
     * @param index
     * @param stream
     * 当前码流获取是从所有参数里面获取，参考JVPlayActivity  623-626行
     * 切换完成后有回调：回调如下，亦可参考JVPlayActivity  357-370行 case JVNetConst.CALL_CATEYE_SENDDATA: {
     * //流媒体猫眼，设置协议回调
            case JVNetConst.CALL_CATEYE_SENDDATA: {
            try {
            org.json.JSONObject object = new org.json.JSONObject(obj.toString());
            int nType = object.getInt("nPacketType");
            int nCmd = object.getInt("nCmd");
            switch (nCmd) {
            //码流切换回调
            case JVNetConst.SRC_REMOTE_CMD: {
            int result = object.getInt("result");
            if (1 == result) {
            Toast.makeText(JVPlayActivity.this, "码流切换成功了！", Toast.LENGTH_LONG).show();
            }
            break;
            }
     */
    public static int streamCatChangeStream(int index, int stream) {
        String changeStr = String.format("nMobileQuality=%d;", stream);
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_CMD,
                0, JVNetConst.SRC_EX_CMD_RESOLUTION, changeStr.getBytes(), changeStr.length());
        Log.e("CATCAT", "streamCatChangeStream=" + sendRes + ";window=" + index+";changeStr="+changeStr);
        return sendRes;
    }


    /**
     * 18.发送声波(新)
     *
     * @param params 格式:wifiName;wifiPass
     * @param times  播放完成的回调是 AppConsts.CALL_GEN_VOICE
     */
    public static void newSendSoundWave(String params, int times) {
        Jni.genSoundConfig(params, times);
        Log.v(TAG, "new sendSoundWave:params=" + params);
    }

    /**
     * 19.声波搜索设备
     *
     */
    public static void searchDevice() {
        Jni.strMedSearchDevice();
        Log.v(TAG, "strMedSearchDevice");
    }

    /**
     * 20.检索远程回放视频列表
     *
     * @param index
     * @param year
     * @param month
     * @param day
     */
    public static int checkStreamRemoteVideo(int index, int year, int month, int day) {
        String date = String.format(Consts.FORMATTER_STREAM_CAT_REMOTE_CHECK_DATE, year,
                month, day, year, month, day);
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_CHECK,
                0, JVNetConst.SRC_EX_CHECK_VIDEO, date.getBytes(), date.length());
        Log.e("CATCAT", "checkStreamRemoteVideo=" + sendRes + ";window=" + index);
        return sendRes;
    }

    /**
     * 21.检索远程回放图片列表
     *
     * @param index
     * @param year
     * @param month
     * @param day
     */
    public static int checkStreamRemoteImage(int index, int year, int month, int day) {

        String date = String.format(Consts.FORMATTER_STREAM_CAT_REMOTE_CHECK_DATE, year,
                month, day, year, month, day);
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_CHECK,
                0, JVNetConst.SRC_EX_CHECK_PCITURE, date.getBytes(), date.length());
        Log.e("CATCAT", "checkStreamRemoteImage=" + sendRes + ";window=" + index);
        return sendRes;
    }


    /**
     * 22.流媒体猫眼取消下载
     *
     * @param index
     */
    public static int cancelStreamCatDownload(int index) {
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_DOWNLOAD,
                0, JVNetConst.SRC_EX_RD_CMD_UPLOADBREAK, null, 0);
        Log.e("CATCAT", "cancelStreamCatDownload=" + sendRes + ";window=" + index);
        return sendRes;
    }

    /**
     * 23.流媒体猫眼请求下载
     *
     * @param index
     */
    public static int startStreamCatDownload(int index,String filePath) {
        String param = String.format(Consts.FORMATTER_STREAM_CAT_DOWNLOAD_PATH,filePath);
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_DOWNLOAD,
                0, JVNetConst.SRC_EX_RD_REQ_DOWNLOAD, param.getBytes(), param.length());
        Log.e("CATCAT", "startStreamCatDownload=" + sendRes + ";window=" + index+";param="+param);
        return sendRes;
    }

    /**
     * 24.流媒体猫眼请求开始远程回放
     * @param index
     * @return
     */
    public static int startStreamCatRemotePlay(int index,String filePath){
        String param = String.format(Consts.FORMATTER_STREAM_CAT_DOWNLOAD_PATH,filePath);
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_PLAY,
                0, JVNetConst.SRC_EX_RP_REQ_PLAY, param.getBytes(), param.length());
        Log.e("CATCAT", "startStreamCatRemotePlay=" + sendRes + ";window=" + index + ";param=" + param);
        return sendRes;
    }

    /**
     * 25.流媒体猫眼请求停止远程回放
     * @param index
     * @return
     */
    public static int stopStreamCatRemotePlay(int index){
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_PLAY,
                0, JVNetConst.SRC_EX_RP_CMD_PLAYSTOP, null, 0);
        Log.e("CATCAT", "stopStreamCatRemotePlay=" + sendRes + ";window=" + index);
        return sendRes;
    }


    /**
     * 26.流媒体猫眼 暂停 远程回放
     * @param index
     * @return
     */
    public static int pauseStreamCatRemotePlay(int index){
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_PLAY,
                0, JVNetConst.SRC_EX_RP_CMD_PLAYPAUSE, null, 0);
        Log.e("CATCAT", "pauseStreamCatRemotePlay=" + sendRes + ";window=" + index);
        return sendRes;
    }

    /**
     * 27.流媒体猫眼 继续 远程回放
     * @param index
     * @return
     */
    public static int goonStreamCatRemotePlay(int index){
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_PLAY,
                0, JVNetConst.SRC_EX_RP_CMD_PLAYGOON, null, 0);
        Log.e("CATCAT", "goonStreamCatRemotePlay=" + sendRes + ";window=" + index);
        return sendRes;
    }


    /**
     * 28.流媒体猫眼是否启用远程回放
     * @param index
     * @param enable true:启用   false:不启用
     * @return
     */
    public static boolean enableStreamCatRemotePlay(int index,boolean enable){
        boolean enableRes = Jni.strMedPlayback(index, enable);
        Log.e("CATCAT", "enableStreamCatRemotePlay:enable=" + enable + ";enableRes=" + enableRes);
        return enableRes;
    }

    /**
     * 29.流媒体猫眼远程回放调节播放进度
     *
     * @param index
     * @param seekProgress
     */
    public static int streamCatSeekTo(int index, int seekProgress) {
        String param = String.format(Consts.FORMATTER_STREAM_CAT_SEEKPOS,seekProgress);
        int sendRes = Jni.strMedSendData(index, JVNetConst.SRC_REMOTE_PLAY,
                0, JVNetConst.SRC_EX_RP_CMD_PLAYSEEK, param.getBytes(), param.length());
        Log.e("CATCAT", "streamCatSeekTo=" + sendRes + ";seekProgress=" + seekProgress);
        return sendRes;
    }

    /**
     * 30.设置下载路径
     * @param fileFullPath
     * @return
     */
    public static void setDownloadFilePath(String fileFullPath){
        Jni.setDownloadFileName(fileFullPath);
    }

    /**
     * 31.暂停底层显示
     *
     * @param index
     */
    public static boolean pauseSurface(int index) {
        return Jni.pause(index);
    }

    /**
     * 32.恢复底层显示
     *
     * @param index
     * @param surface
     */
    public static boolean resumeSurface(int index, Surface surface) {
        return Jni.resume(index, surface);
    }

    /**
     * 33.获取流媒体猫眼文件列表
     * @param fileStr
     * @param dateStr
     * @return
     */
    public static ArrayList<CatFile> getStreamCatFileList(String fileStr, String dateStr) {
        ArrayList<CatFile> dataList = new ArrayList<CatFile>();

        try {
            dateStr = dateStr.replace("-", "");
            if (null == fileStr || "".equalsIgnoreCase(fileStr)){
                return dataList;
            }
            String[] fileArray = fileStr.split(";");
            for(int i = 0 ; i < fileArray.length ; i++){
                CatFile cf = new CatFile();
                String fileString = fileArray[i];
                String thumbnailKind = String.valueOf(fileString.charAt(0));//T or X
                String meidaKind = String.valueOf(fileString.charAt(1));//P or V
                String alarmKind = String.valueOf(fileString.charAt(2));//N or A

                cf.setName(String.format("%c%c:%c%c:%c%c",
                        fileString.charAt(5), fileString.charAt(6), fileString.charAt(7),
                        fileString.charAt(8), fileString.charAt(9), fileString.charAt(10)));

                Log.e("fullList-" + i, "name=" + cf.getName());
                cf.setFileDate(String.format("%c%c:%c%c:%c%c",
                        fileString.charAt(5), fileString.charAt(6), fileString.charAt(7),
                        fileString.charAt(8), fileString.charAt(9), fileString.charAt(10)));
                Log.e("fullList-" + i, "date=" + cf.getFileDate());

                //文件类型
                if ("P".equalsIgnoreCase(meidaKind)) {
                    cf.setMediaKind(CatFile.MEDIA_PICTURE);
                    cf.setFilePath(File.separator + "mnt" + File.separator + "misc" + File.separator + dateStr + File.separator + String.format("%c%c%c%c%c%c%c%c%c",
                            fileString.charAt(2), fileString.charAt(3), fileString.charAt(4), fileString.charAt(5), fileString.charAt(6),
                            fileString.charAt(7), fileString.charAt(8), fileString.charAt(9),
                            fileString.charAt(10)) + ".jpg");
                } else if ("V".equalsIgnoreCase(meidaKind)) {
                    cf.setMediaKind(CatFile.MEDIA_VIDEO);
                    cf.setFilePath(File.separator + "mnt" + File.separator + "misc" + File.separator + dateStr + File.separator + String.format("%c%c%c%c%c%c%c%c%c",
                            fileString.charAt(2), fileString.charAt(3), fileString.charAt(4), fileString.charAt(5), fileString.charAt(6),
                            fileString.charAt(7), fileString.charAt(8), fileString.charAt(9),
                            fileString.charAt(10)) + ".mp4");
                }

                //正常文件，报警文件
                if ("N".equalsIgnoreCase(alarmKind)) {
                    cf.setFileKind(CatFile.FILE_NORMAL);
                } else if ("A".equalsIgnoreCase(alarmKind)) {
                    cf.setFileKind(CatFile.FILE_ALARM);
                }

                //
                if ("T".equalsIgnoreCase(thumbnailKind)){
                    cf.setThumbnailPath(File.separator + "mnt" + File.separator + "misc" + File.separator + dateStr + File.separator + String.format("%c%c%c%c%c%c%c%c%c%c%c",
                            fileString.charAt(0), fileString.charAt(1),fileString.charAt(2), fileString.charAt(3), fileString.charAt(4), fileString.charAt(5), fileString.charAt(6),
                            fileString.charAt(7), fileString.charAt(8), fileString.charAt(9),
                            fileString.charAt(10)) + ".jpg");
                } else {
                    cf.setThumbnailPath("");
                }

                Log.e("fullList-" + i, "ThumbnailPath=" + cf.getThumbnailPath());
                cf.setIndex(dataList.size());
                dataList.add(cf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }




    //获取全部字段信息
    public static int getStreamCatDataAll(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_PARAM_ALL,
                0, JVNetConst.SRC_EX_GETPARAM, null, 0);
        Log.e("CATCAT","streamCatSendData_res="+sendRes+";window="+window);
        return sendRes;
    }

    //设置感应门铃按键灯开关：0关，1开
    public static int setStreamBellLightStatus(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_DISPLAY,
                0, JVNetConst.SRC_EX_DISPLAY_BELLLIGHT, data.getBytes(), data.length());
        Log.e("CATCAT","setBellLightStatus="+sendRes+";window="+window+";type:"+data);
        return sendRes;
    }

    //流媒体设置猫眼休眠时间
    public static int setStreamSuspentTime(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_DISPLAY,
                0, JVNetConst.SRC_EX_DISPLAY_SUSPENDTIME, data.getBytes(), data.length());
        Log.e("CATCAT","setStreamSuspentTime="+sendRes+";window="+window+";type:"+data);
        return sendRes;
    }
    //设置报警类型：0图片，1视频
    public static int setStreamAlarmType(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_INTELLIGENCE,
                0, JVNetConst.SRC_EX_INTELLIGENCE_ALARMTYPE, data.getBytes(), data.length());
        Log.e("CATCAT","setStreamAlarmType="+sendRes+";window="+window+";type:"+data);
        return sendRes;
    }

    //设置红外感应开关
    public static int setStreamPirEnable(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_INTELLIGENCE,
                0, JVNetConst.SRC_EX_INTELLIGENCE_PIR, data.getBytes(), data.length());
        Log.e("CATCAT","setStreamPirEnable="+sendRes+";window="+window+";type:"+data);
        return sendRes;
    }

    //设置重力感应
    public static int setStreamGSensorEnable(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_INTELLIGENCE,
                0, JVNetConst.SRC_EX_INTELLIGENCE_GSENSOR, data.getBytes(), data.length());
        Log.e("CATCAT","setStreamGSensorEnable="+sendRes+";window="+window+";type:"+data);
        return sendRes;
    }

    //设置移动侦测开关
    public static int setStreamMDetect(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_INTELLIGENCE,
                0, JVNetConst.SRC_EX_INTELLIGENCE_MDETECT, data.getBytes(), data.length());
        Log.e("CATCAT","setStreamMDetect="+sendRes+";window="+window+";type:"+data);
        return sendRes;
    }

    //设备重启
    public static int streamRestart(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_ABOUTEYE,
                0, JVNetConst.SRC_EX_ABOUT_REBOOT, null, 0);
        Log.e("CATCAT","streamRestart="+sendRes+";window="+window);
        return sendRes;
    }

    //设备恢复出厂设置
    public static int streamReset(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_ABOUTEYE,
                0, JVNetConst.SRC_EX_ABOUT_FORMAT, null, 0);
        Log.e("CATCAT","streamReset="+sendRes+";window="+window);
        return sendRes;
    }

    //设备关机
    public static int streamShutdown(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_ABOUTEYE,
                0, JVNetConst.SRC_EX_ABOUT_SHUTDOWN, null, 0);
        Log.e("CATCAT","streamShutdown="+sendRes+";window="+window);
        return sendRes;
    }

    //获取猫眼信息
    public static int getStreamCatInfos(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_ABOUTEYE,
                0, JVNetConst.SRC_EX_ABOUT_REFRESH, null, 0);
        Log.e("CATCAT","getStreamCatInfos="+sendRes+";window="+window);
        return sendRes;
    }

    //获取设备SD卡信息
    public static int getStreamCatSDinfo(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_STORAGE,
                0, JVNetConst.SRC_EX_STORAGE_REFRESH, null, 0);
        Log.e("CATCAT","getStreamCatSDinfo="+sendRes+";window="+window);
        return sendRes;
    }

    //SD卡设置存储分辨率
    public static int setStreamSDResolution(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_STORAGE,
                0, JVNetConst.SRC_EX_STORAGE_RESOLUTION, data.getBytes(), data.length());
        Log.e("CATCAT","setStreamSDResolution="+sendRes+";window="+window+";data="+data);
        return sendRes;
    }

    //SD卡设置录像时间
    public static int setStreamSDRecordTime(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_STORAGE,
                0, JVNetConst.SRC_EX_STORAGE_RECORDTIME, data.getBytes(), data.length());
        Log.e("CATCAT","setStreamSDRecordTime="+sendRes+";window="+window+";data="+data);
        return sendRes;
    }

    //SD卡设置自动覆盖开关
    public static int setStreamSDAutoSwitch(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_STORAGE,
                0, JVNetConst.SRC_EX_STORAGE_AUTOSWITCH, data.getBytes(), data.length());
        Log.e("CATCAT","setStreamSDAutoSwitch="+sendRes+";window="+window+";data="+data);
        return sendRes;
    }

    //SD卡格式化
    public static int setStreamSDFormat(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_STORAGE,
                0, JVNetConst.SRC_EX_STORAGE_CMD_FORMAT, null, 0);
        Log.e("CATCAT","setStreamSDAutoSwitch="+sendRes+";window="+window);
        return sendRes;
    }

    //获取设备时间信息
    public static int getStreamCatTime(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_TIME,
                0, JVNetConst.SRC_EX_GETSYSTIME, null, 0);
        Log.e("CATCAT", "getStreamCatTime=" + sendRes + ";window=" + window);
        return sendRes;
    }

    //修改猫眼所在时区
    public static int setStreamCatZone(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_TIME,
                0, JVNetConst.SRC_EX_SETTIME_ZONE, data.getBytes(), data.length());
        Log.e("CATCAT", "setStreamSDResolution=" + sendRes + ";window=" + window + ";data=" + data);
        return sendRes;
    }

    //修改时间格式
    public static int setStreamCatTimeFormat(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_TIME,
                0, JVNetConst.SRC_EX_SETTIME_FORMAT, data.getBytes(), data.length());
        Log.e("CATCAT", "setStreamSDResolution=" + sendRes + ";window=" + window + ";data=" + data);
        return sendRes;
    }

    //修改猫眼网络校对开关
    public static int setStreamCatSntp(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_TIME,
                0, JVNetConst.SRC_EX_SETTIME_SNTP, data.getBytes(), data.length());
        Log.e("CATCAT", "setStreamSDResolution=" + sendRes + ";window=" + window + ";data=" + data);
        return sendRes;
    }

    //设置设备时间
    public static int setStreamCatTime(int window, String data) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_TIME,
                0, JVNetConst.SRC_EX_SETSYSTIME, data.getBytes(), data.length());
        Log.e("CATCAT", "setStreamCatTime=" + sendRes + ";window=" + window);
        return sendRes;
    }

    //向设备请求升级
    public static int cmdStreamCatUpdateInfo(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_FIRMUP,
                0, JVNetConst.SRC_EX_FIRMUP_REQ, null, 0);
        Log.e("CATCAT", "getStreamCatUpdateInfo=" + sendRes + ";window=" + window);
        return sendRes;
    }

    //发送命令给设备开始下载升级文件
    public static int cmdStreamCatUpdateDownload(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_FIRMUP,
                0, JVNetConst.SRC_EX_UPLOAD_START_STREAM, null, 0);
        Log.e("CATCAT", "getStreamCatUpdateDownload=" + sendRes + ";window=" + window);
        return sendRes;
    }

    //发送命令给设备取消下载升级文件
    public static int cmdStreamCatUpdateCancel(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_FIRMUP,
                0, JVNetConst.SRC_EX_UPLOAD_CANCEL_STREAM, null, 0);
        Log.e("CATCAT", "getStreamCatUpdateCacel=" + sendRes + ";window=" + window);
        return sendRes;
    }

    //发送命令给设备获取下载升级文件进度
    public static int cmdStreamCatUpdateProgress(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_FIRMUP,
                0, JVNetConst.SRC_EX_UPLOAD_PROGRESS, null, 0);
        Log.e("CATCAT", "getStreamCatUpdateProgress=" + sendRes + ";window=" + window);
        return sendRes;
    }

    //发送命令给设备烧写程序
    public static int cmdStreamCatUpdateFirmUp(int window) {
        int sendRes = Jni.strMedSendData(window, JVNetConst.SRC_FIRMUP,
                0, JVNetConst.SRC_EX_FIRMUP_START, null, 0);
        Log.e("CATCAT", "getStreamCatUpdateProgress=" + sendRes + ";window=" + window);
        return sendRes;
    }
}
