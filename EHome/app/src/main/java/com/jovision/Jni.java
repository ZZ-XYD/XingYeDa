
package com.jovision;

/**
 * Created by juyang on 16/3/22.
 */
public class Jni {

    /**
     * 1.初始化，参考 {@link JVSUDT#JVC_InitSDK(int, Object)}
     *
     * @param handle 回调句柄，要传 MainApplication 的实例对象哦，因为回调方式是：<br />
     *               {@link MainApplication#onJniNotify(int, int, int, Object)}
     * @param port   本地端口
     * @param path   日志路径
     * @param strIP  手机外网ip
     * @return true：成功，false：失败
     */
    public static native boolean init(Object handle, int port, String path, String strIP);

    /**
     * 2.卸载，参考 {@link JVSUDT#JVC_ReleaseSDK()}
     */
    public static native void deinit();

    /**
     * 3.（新增接口）获取底层库版本,为了防止库用错，建议将该版本号打印出来
     *
     * @return json: {"jni":"xx","net":"xx"}
     */
    public static native String getVersion();

    /**
     * 4.启用底层日志打印，参考 {@link JVSUDT#JVC_EnableLog(boolean)}
     *
     * @param enable
     */
    public static native void enableLog(boolean enable);

    /**
     * 5.（新增接口）删除底层保存的错误日志
     */
    public static native void deleteLog();

    /**
     * 6.新 连接，参考
     * {@link JVSUDT#JVC_Connect(int, int, String, int, String, String, int, String, boolean, int, boolean, int, Object)}
     *
     * @param window        窗口索引，从 0 开始
     * @param channel       设备通道，从 1 开始
     * @param ip            设备IP
     * @param port          设备端口
     * @param username      设备用户名
     * @param password      设备密码
     * @param cloudSeeId    设备云视通号码（例如：A361）
     * @param groupId       设备编组（例如：A）
     * @param isLocalDetect
     * @param turnType
     * @param isPhone
     * @param connectType
     * @param surface
     * @param isVip
     * @param isTcp
     * @param isAp
     * @param isTryOmx
     * @param thumbName
     * @return 连接结果，成功时返回窗口索引，失败时返回原因值，以下是返回的错误信息
     * #define BAD_HAS_CONNECTED	-1 （未初始化网络库也报此错误）
     * #define BAD_CONN_OVERFLOW	-2
     *  #define BAD_NOT_CONNECT		-3
     *  #define BAD_ARRAY_OVERFLOW	-4
     *  #define BAD_CONN_UNKOWN		-5
     */
    public static native int connect(int window,
                                     int channel,
                                     String ip,
                                     int port,
                                     String username,
                                     String password,
                                     int cloudSeeId,
                                     String groupId,
                                     boolean isLocalDetect,
                                     int turnType,
                                     boolean isPhone,
                                     int connectType,
                                     Object surface,
                                     boolean isVip,
                                     boolean isTcp,
                                     boolean isAp,
                                     boolean isTryOmx,
                                     String thumbName);

    /**
     * 7.暂停底层显示
     *
     * @param window 窗口索引
     * @return true：成功，false：失败
     */
    public static native boolean pause(int window);

    /**
     * 8.恢复底层显示
     *
     * @param window  窗口索引
     * @param surface
     * @return true：成功，false：失败
     */
    public static native boolean resume(int window, Object surface);

    /**
     * 9.断开视频连接，参考 {@link JVSUDT#JVC_DisConnect(int)}
     *
     * @param window 窗口索引
     * @return true：成功，false：失败
     */
    public static native boolean disconnect(int window);

    /**
     * 10.发送字节数据，参考 {@link JVSUDT#JVC_SendData(int, byte, byte[], int)}
     *
     * @param window  窗口索引
     * @param uchType
     * @param data
     * @param size
     * @return true：成功，false：失败
     */
    public static native boolean sendBytes(int window, byte uchType,
                                           byte[] data, int size);

    /**
     * 11.发送整数数据（远程回放进度调节），参考
     * {@link JVSUDT#JVC_SendPlaybackData(int, byte, int, int)} 实际调用
     * {@link #sendCmd(int, byte, byte[], int)}
     *
     * @param window  窗口索引
     * @param uchType
     * @param data
     */
    public static native boolean sendInteger(int window, byte uchType, int data);

    /**
     * 12.修改指定窗口播放标识位，是否启用远程回放，参考 {@link JVSUDT#ChangePlayFalg(int, int)}
     *
     * @param window 窗口索引
     * @param enable true：启用远程回放，false：不启用远程回放
     * @return
     */
    public static native boolean enablePlayback(int window, boolean enable);

    /**************** 2015-02-09 V2.2.0 新增功能 ********************/

    /**
     * 13.初始化音频编码（弃用）
     *
     * @param type         类型，amr/alaw/ulaw，参考 {@link AppConsts#JAE_ENCODER_SAMR},
     *                     {@link AppConsts#JAE_ENCODER_ALAW},
     *                     {@link AppConsts#JAE_ENCODER_ULAW}
     * @param sampleRate
     * @param channelCount
     * @param bitCount
     * @param block        PCM 640
     * @return
     */
    public static native boolean initAudioEncoder(int type, int sampleRate,
                                                  int channelCount, int bitCount, int block);

    /**
     * 14.编码一帧（弃用）
     *
     * @param data
     * @return 失败的话返回 null
     */
    public static native byte[] encodeAudio(byte[] data);

    /**
     * 15.销毁音频编码，如果要切换编码参数，必须销毁的重新创建（弃用）
     *
     * @return
     */
    public static native boolean deinitAudioEncoder();

    /**
     * 16.发送字符串数据
     *
     * @param window   窗口索引
     * @param uchType  发送类型
     * @param isExtend 是否扩展消息
     * @param count    扩展包数量
     * @param type     扩展消息类型
     * @param data     数据
     */
    public static native boolean sendString(int window, byte uchType,
                                            boolean isExtend, int count, int type, String data);

    /**
     * 17.发送超级字节数组
     *
     * @param window
     * @param uchType
     * @param isExtend
     * @param count
     * @param type
     * @param p1
     * @param p2
     * @param p3
     * @param data
     * @param size
     * @return
     */
    public static native boolean sendSuperBytes(int window, byte uchType,
                                                boolean isExtend, int count, int type, int p1, int p2, int p3,
                                                byte[] data, int size);

    /**
     * 18. 发送原始数据
     *
     * @param window
     * @param data_type
     * @param packet_type
     * @param packet_count
     * @param extend_type
     * @param extend_p1
     * @param extend_p2
     * @param extend_p3
     * @param data
     * @param size
     * @return
     */
    public static native boolean sendPrimaryBytes(int window, byte data_type,
                                                  int packet_type, int packet_count, int extend_type, int extend_p1,
                                                  int extend_p2, int extend_p3, byte[] data, int size);

    /**
     * 19.老声波：生成声波配置语音数据 生成声波配置数据，重复多次会阻塞执行
     *
     * @param data：WiFi名称和密码
     * @param times：声波响的次数
     */
    public static native void genVoice(String data, int times);

    /**
     * 20.发送聊天命令，参考 {@link JVSUDT#JVC_SendTextData(int, byte, int, int)}
     *
     * @param window  窗口索引
     * @param uchType
     * @param size
     * @param flag
     */
    public static native boolean sendTextData(int window, byte uchType,
                                              int size, int flag);

    /**
     * 21.开始录制（本地录像），参考
     * {@link JVSUDT#StartRecordMP4(String, int, int, int, int, int, double, int)}
     *
     * @param window       窗口索引
     * @param path         文件保存路径
     * @param enableVideo  是否录制视频
     * @param enableAudio  是否录制音频
     * @param audioCodecID 0为amr音频格式封装 1原格式封装 接口新增参数；amr大部分手机播放器均可以播放此格式音频，原格式封装则支持的较少
     * @return
     */
    public static native boolean startRecord(int window, String path,
                                             boolean enableVideo, boolean enableAudio, int audioCodecID,boolean isTwoRecord);


    /**
     * 22.检查对应窗口是否处于录像状态， 现在只有单路可用
     *
     * @param window 窗口索引
     * @return true 正在录像，false 没在录像
     */
    public static native boolean checkRecord(int window);

    /**
     * 23.停止录制，参考 {@link JVSUDT#StopRecordMP4(int)}
     *
     * @return
     */
    public static native boolean stopRecord();

    /**
     * 24.修改指定窗口音频标识位
     *
     * @param window 窗口索引
     * @param enable 是否播放（ Normaldata 的）音频数据 true：开启声音  false：关闭声音
     * @return
     */
    public static native boolean enablePlayAudio(int window, boolean enable);

    /**
     * 25.设置 AP，参考 {@link JVSUDT#JVC_ManageAP(int, byte, String)}
     *
     * @param window  窗口索引
     * @param uchType
     * @param json
     */
    public static native boolean setAccessPoint(int window, byte uchType,
                                                String json);

    /**
     * 26.获取指定窗口是否正在播放音频
     *
     * @param window 窗口索引
     * @return true:正在监听，false：没有正在监听
     */
    public static native boolean isPlayAudio(int window);

    /**
     * 27.开启小助手（快速链接服务），参考 {@link JVSUDT#JVC_EnableHelp(boolean, int)}
     *
     * @param enable
     * @param typeId   enable == true 时
     *                 <ul>
     *                 <li>1: 使用者是开启独立进程的云视通小助手</li>
     *                 <li>2: 使用者是云视通客户端，支持独立进程的云视通小助手</li>
     *                 <li>3: 使用者是云视通客户端，不支持独立进程的云视通小助手</li>
     *                 </ul>
     * @param maxLimit 允许最大限制
     * @return
     */
    public static native boolean enableLinkHelper(boolean enable, int typeId,
                                                  int maxLimit);

    /**
     * 28.给设备设置连接小助手，参考 {@link JVSUDT#JVC_SetHelpYSTNO(byte[], int)}
     *
     * @param json [{gid: "A", no: 361, channel: 1, name: "abc", pwd:
     *             "123"},{gid: "A", no: 362, channel: 1, name: "abc", pwd:
     *             "123"}]
     * @return
     */
    public static native boolean setLinkHelper(String json);

    /**
     * 29.开启广播，参考 {@link JVSUDT#JVC_StartLANSerchServer(int, int)}
     *
     * @param localPort  默认 9400
     * @param serverPort 默认 6666
     * @param strIP      手机的ip地址
     * @return
     */
    public static native int searchLanServer(int localPort, int serverPort, String strIP);

    /**
     * 30.停止搜索局域网服务端，参考 {@link JVSUDT#JVC_StopLANSerchServer()}
     */
    public static native void stopSearchLanServer();

    /**
     * 31.搜索局域网设备，参考 搜索本局域网
     * {@link JVSUDT#JVC_MOLANSerchDevice(String, int, int, int, String, int)}
     * 跨网段广播
     *
     * @param group
     * @param cloudSeeId
     * @param cardType
     * @param variety
     * @param deviceName
     * @param timeout    单位是毫秒
     * @param frequence
     * @return
     */
    public static native int searchLanDevice(String group, int cloudSeeId,
                                             int cardType, int variety, String deviceName, int timeout,
                                             int frequence);

    /**
     * 32.查询某个设备是否被搜索出来 局域网本网段广播
     *
     * @param groudId    组标识
     * @param cloudSeeId 云视通编号
     * @param timeout    超时时间，毫秒
     * @return 调用是否成功，等回调
     */
    public static native boolean queryDevice(String groudId, int cloudSeeId,
                                             int timeout);

    /**
     * 33.恢复底层音频播放
     *
     * @param window 窗口索引
     * @return
     */
    public static native boolean resumeAudio(int window);

    /**
     * 34.暂停底层音频播放
     *
     * @param window 窗口索引
     * @return
     */
    public static native boolean pauseAudio(int window);

    /**
     * 35.底层MP4播放初始化功能
     *
     * @param
     * @return
     */
    public static native int Mp4Init();

    /**
     * 36.底层MP4播放初始化功能
     *
     * @param uri
     * @return
     */
    public static native int SetMP4Uri(String uri);

    /**
     * 37.底层MP4播放准备接口，主要解析MP4文件信息，并且通过回调返回给应用层
     *
     * @return 0：OK ;1:正在播放 ;2：底层播放线程正在退出，需要等待完全退出才能继续播放
     */
    public static native int Mp4Prepare();

    /**
     * 38.底层MP4开始播放接口
     *
     * @param surface
     * @return 0：OK 其他失败
     */
    public static native int Mp4Start(Object surface);

    /**
     * 39.底层MP4停止播放接口
     *
     * @param StopSeconds 停止播放的时间点(秒)
     * @return
     */
    public static native int Mp4Stop(int StopSeconds);

    /**
     * 40.底层MP4播放库销毁释放资源
     *
     * @return
     */
    public static native int Mp4Release();

    /**
     * 41.底层MP4播放暂停接口，与MP4Resume对应
     *
     * @return
     */
    public static native int Mp4Pause();

    /**
     * 42.底层MP4继续播放接口，与Mp4Pause对应
     *
     * @return
     */
    public static native int Mp4Resume();

    /**
     * 43.停止广播
     * <p/>
     * STOP lansearch return：1:success 0:failed
     */
    public static native int StopMobLansearch();

    /**
     * 44.截图抓拍
     *
     * @param window  窗口索引
     * @param name    待保存的文件名
     * @param quality 画面质量
     * @return
     */
    public static native boolean screenshot(int window, String name, int quality);

    /**
     * 45.设置本地的服务器
     *
     * @param pGroup
     * @param pServer
     * @return 0:成功 其他：失败
     */
    public static native int SetSelfServer(
            String pGroup, String pServer);

    /**
     * 46.设置缩略图信息
     *
     * @param width   缩略图宽
     * @param quality 图像质量
     */
    public static native void setThumb(int width, int quality);

    /**
     * 47.是否显示统计
     *
     * @param on true:开启统计 false：关闭统计
     */
    public static native void setStat(boolean on);

    /**
     * 48.取消下载，删除正在下载的文件
     */
    public static native void cancelDownload();

    /**
     * 49.设置窗口颜色
     *
     * @param window 窗口索引
     * @param red    红，0~1
     * @param green  绿，0~1
     * @param blue   蓝，0~1
     * @param alpha  透明，0~1
     * @return
     */
    public static native boolean setColor(int window, float red, float green,
                                          float blue, float alpha);

    /**
     * 51.获取下载文件路径
     *
     * @return
     */
    public static native String getDownloadFileName();

    /**
     * 50.设置下载文件路径
     *
     * @param fileName
     */
    public static native void setDownloadFileName(String fileName);

    /**
     * 52.翻转视频
     *
     * @param window  窗口索引
     * @param uchType
     * @param cmd
     */
    public static native boolean rotateVideo(int window, byte uchType,
                                             String cmd);

    /**
     * 设置mtu的调用步骤:<br/>
     * 1、调用停止小助手函数 -> StopHelp();<br/>
     * 2、设置MTU -> SetMtu(int mtu);<br/>
     * 3、打开小助手函数 -> enableLinkHelper;<br/>
     * 4、把所有设备设置到小助手函数中 -> setLinkHelper;<br/>
     * 以上步骤操作后,要记住设置mut状态,下次重启软件,初始化网络sdk后 ,立即设置mtu,然后在打开小助手,设置小助手.
     */

    /**
     * 53.停止小助手<br/>
     */
    public static native int StopHelp();

    /**
     * 54.设置MTU<br/>
     * nMtu可以设置的值只有700,1400（七百，1千4百）
     *
     * @param nMtu
     * @return 1:成功 0：失败
     */
    public static native int SetMTU(int nMtu);

    /**
     * 55.获取已设置的云视通列表<br/>
     * 参考 {@link JVSUDT#JVC_GetHelpYSTNO(byte[], int)}
     *
     * @return json [{cno: "A361", enable: false},{no: "A362", enable: false}]
     */
    public static native String getAllDeviceStatus();

    // --------------------------------------------------------
    // ## libjvpush.so 开始
    // --------------------------------------------------------
    /**
     * 初始化并且连接离线推送SDK接口 平台参数<br/>
     * #define BIZ_PUSH_IOS 0x00<br/>
     * #define BIZ_PUSH_ANDROID 0x10<br/>
     * #define BIZ_PUSH_PC_WIN 0x21<br/>
     * #define BIZ_PUSH_PC_LINUX 0x22<br/>
     * #define BIZ_PUSH_PC_MAX 0x23
     */

    /**
     * 56. 初始化离线推送SDK<br/>
     * init sdk in:上下文 object
     */
    public static native int initSdk(Object CallbackHandle);

    /**
     * 57.开始<br/>
     * return :0:success -1:failed
     */
    public static native int start(Byte platform, int appid, String token, String url);

    /**
     * 58.停止<br/>
     * return :0:success -1:failed
     */
    public static native int stop();

    // --------------------------------------------------------
    // ## libjvpush.so 结束
    // --------------------------------------------------------

    /**
     * 59.设置显示图像的顶点坐标(坐标系原点在 Surface 左下顶点)和长宽
     *
     * @param window 窗口索引
     * @param left   图像左坐标
     * @param bottom 图像底坐标
     * @param width  图像宽
     * @param height 图像高
     * @return
     */
    public static native boolean setViewPort(int window, int left, int bottom,
                                             int width, int height);

    /**
     * 60.新声波：生成声波配置语音数据 生成声波配置数据，重复多次会阻塞执行
     *
     * @param data：WiFi名称和密码
     * @param times：声波响的次数
     */
    public static native void genSoundConfig(String data, int times);

    /****************************************************************************
     * 62.猫眼设备唤醒接口 名称 : JVC_StartBroadcastSelfServer 功能 : 开启自定义广播服务 回调使用之前方式
     * 类型是0xB7 参数 : [IN] nLPort 本地服务端口，<0时为默认9700 [IN] nServerPort
     * 设备端服务端口，<=0时为默认9108,建议统一用默认值与服务端匹配 返回值: TRUE/FALSE 其他 :
     *****************************************************************************/
    public static native int startBCSelfServer(int localPort, int serverPort);

    /****************************************************************************
     * 62.停止猫眼唤醒接口 名称 : JVC_StopBroadcastSelfServer 功能 : 停止自定义广播服务 参数 : 无 返回值: 无
     * 其他 : 无
     *****************************************************************************/
    public static native void stopBCSelfServer();

    /****************************************************************************
     * 63.猫眼回调需要调用接口 名称 : JVC_SendSelfDataOnceFromBC 功能 :
     * 此方法要在StartBroadcastSelfServer回调返回时调用 从自定义广播套接字发送一次UDP消息 参数 : [IN] pBuffer
     * 净载数据 类型（4字节）+云视通号码（4字节） [IN] nSize 净载数据长度 [IN] pchDeviceIP 目的IP地址 [IN]
     * nLocalPort 目的端口 返回值: 无 其他 :
     *****************************************************************************/
    public static native void sendSelfDataOnceFromBC(byte[] buffer, int size, String ip, int port);

    /**
     * 64.连接流媒体
     *
     * @param window
     * @param url
     * @param surface
     * @param isTryOmx
     * @param thumbName
     * @param timeOut   超时时间：为毫秒：比如10秒超时，需填写：10*1000
     * @return
     */
    public static native int connectRTMP(int window, String url,
                                         Object surface, boolean isTryOmx, String thumbName, int timeOut);

    /**
     * 65.关闭流媒体
     *
     * @param window
     * @return
     */
    public static native boolean shutdownRTMP(int window);

    /**
     * 66.开始录音接口
     *
     * @param window
     * @return
     */
    public static native boolean recordAndsendAudioData(int window);

    /**
     * 67.停止录音接口
     *
     * @param window
     * @return
     */
    public static native boolean stopRecordAudioData(int window);

    /**
     * 68.清理小助手缓存
     */
    public static native void clearCache();

    /**
     * 69.默认播放器是开启aec和降噪，如不需要 调用此接口重置player 在recordAndsendAudioData之前
     *
     * @param window
     * @param isAec
     * @param isDenoise
     */
    public static native void resetAecDenoise(int window, boolean isAec, boolean isDenoise);

    /**
     * 69.手机改变网络状态时更换绑定的IP地址 2015年12月4日
     *
     * @param strNewIP
     * @return
     */
    public static native int ChangeMobileIP(String strNewIP);

    /**
     * 70.移除小助手
     * [METHOD] HelperRemove
     * [IN] pGroup 编组号，编组号+nYSTNO可确定唯一设备
     * [IN] NYST 搜索具有某云视通号码的设备，>0有效
     *  * [RETURN] no
     */
    public static native int HelperRemove(String group, int yst);


    /**
     * 71.播歌曲  新增网络接口
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @return
     */
    public static native int SendFile(String path, String fileName);


    /**
     * 72.发送命令，参考 {@link JVSUDT#JVC_SendCMD(int, byte, byte[], int)}
     * // [Neo] TODO 未验证
     *
     * @param window  窗口索引
     * @param uchType
     * @param data
     * @param size
     * @return
     */
    public static native int sendCmd(int window, byte uchType, byte[] data,
                                     int size);



//    JNIEXPORT jboolean JNICALL Java_com_jovision_Jni_setSurfaceStat(JNIEnv* env,
//                                                                    jclass clazz, jint window, jint windowNum, jint isHorizontal)
//    两个窗口
//    JNIEXPORT jboolean JNICALL Java_com_jovision_Jni_setSurfaceStat(JNIEnv* env,
//                                                                    jclass clazz, jint window,  2, 0)
//    一个窗口
//    JNIEXPORT jboolean JNICALL Java_com_jovision_Jni_setSurfaceStat(JNIEnv* env,
//                                                                    jclass clazz, jint window,  2, 0)


    /**
     * 73.设置视频播放显示几个窗口
     * @param window
     * @param windowNum
     * @param isHorizontal
     * @return
     */
    public static native boolean setSurfaceStat(int window,int windowNum,int isHorizontal);



    /**
     * 5.流媒体连接初始化接口
     * 调用时机：程序启动时调用一次即可,用来初始化。
     *
     * @return 0：成功；-1：失败（失败的原因只有一个:Player_init内部会启动一个线程，启动线程有可能会失败。这个几率可以忽略不计。当然如果真发生这种错误，直接退出软件即可。）
     */

    public static native int strMedPlayerInit(String address);
/* 5.流媒体连接初始化接口
     * 调用时机：程序启动时调用一次即可,用来初始化。
     *
     * @return 0：成功；-1：失败（失败的原因只有一个:Player_init内部会启动一个线程，启动线程有可能会失败。这个几率可以忽略不计。当然如果真发生这种错误，直接退出软件即可。）
     */

    public static native int strMedPlayerInit(String address,String logPath);
    /**
     * 6.新 连接，参考
     * {@link JVSUDT#JVC_Connect(int, int, String, int, String, String, int, String, boolean, int, boolean, int, Object)}
     *
     * @param window        窗口索引，从 0 开始
     * @param channel       设备通道，从 1 开始
     * @param ip            设备IP
     * @param port          设备端口
     * @param username      设备用户名
     * @param password      设备密码
     * @param cloudSeeId    设备云视通号码（例如：A361）
     * @param groupId       设备编组（例如：A）
     * @param isLocalDetect
     * @param turnType
     * @param isPhone
     * @param connectType
     * @param surface
     * @param isVip
     * @param isTcp
     * @param isAp
     * @param isTryOmx
     * @param thumbName
     * @return 连接结果，成功时返回窗口索引，失败时返回原因值，以下是返回的错误信息
     * #define BAD_HAS_CONNECTED	-1 （未初始化网络库也报此错误）
     * #define BAD_CONN_OVERFLOW	-2
     *  #define BAD_NOT_CONNECT		-3
     *  #define BAD_ARRAY_OVERFLOW	-4
     *  #define BAD_CONN_UNKOWN		-5
     */

    /**
     * 6.新 连接，参考
     * {@link JVSUDT#JVC_Connect(int, int, String, int, String, String, int, String, boolean, int, boolean, int, Object)}
     *
     * @param window        窗口索引，从 0 开始
     * @param nType         新添加的连接类型,0 IP  1 号码 2 昵称 3只TCP 4 号码 + 端口
     * @param channel       设备通道，从 1 开始
     * @param ip            设备IP
     * @param port          设备端口
     * @param username      设备用户名
     * @param password      设备密码
     * @param cloudSeeId    设备云视通号码（例如：A361）
     * @param groupId       设备编组（例如：A）
     * @param isLocalDetect
     * @param turnType
     * @param isPhone
     * @param connectType
     * @param surface
     * @param isVip
     * @param chNickName    传null
     * @param isAp
     * @param isTryOmx
     * @param thumbName
     * @param netLibType    传0
     * @return 连接结果，成功时返回窗口索引，失败时返回原因值，以下是返回的错误信息
     * #define BAD_HAS_CONNECTED	-1 （未初始化网络库也报此错误）
     * #define BAD_CONN_OVERFLOW	-2
     *  #define BAD_NOT_CONNECT		-3
     *  #define BAD_ARRAY_OVERFLOW	-4
     *  #define BAD_CONN_UNKOWN		-5
     */
    public static native int connect(int window,
                                     int nType,
                                     int channel,
                                     String ip,
                                     int port,
                                     String username,
                                     String password,
                                     int cloudSeeId,
                                     String groupId,
                                     boolean isLocalDetect,
                                     int turnType,
                                     boolean isPhone,
                                     int connectType,
                                     Object surface,
                                     boolean isVip,
                                     String chNickName,
                                     boolean isAp,
                                     boolean isTryOmx,
                                     String thumbName,
                                     int netLibType);


    /**
     * 6.流媒体设备上线地址获取（此函数会生成一个文件，此文件对上层是透明的。播放库会查询此文件；
     * 当手机新加一个猫眼设备号时，此时还不知道这台猫眼要登录哪一台服务器。
     * 用这个接口即可，查询结果会用回调的方式通知播放库。查询到的猫眼对应的服务器地址，建议都保存在文件中。下次就可以不用再查了。）
     * @param filePath 上线地址存储位置：手机sd卡任意存在的文件夹路径
     * @param deviceCount 猫眼设备数量：ystNumArray的length
     * @param ystNumArray 猫眼设备号码数组
     * @return -1：失败  0：成功需要等待CALL_CATEYE_ADDRESS 0xD1 回调  1：成功，无需等回调

     */
    public static native int strMedAddress(String filePath,int deviceCount,String[] ystNumArray);

    /**
     * 7.查询猫眼是否在线
     * @param ystNum 猫眼云视通号码
     * @param filePath 文件存放地址：路径同接口6
     * @return 0：成功   -1：失败 CALL_CATEYE_ONLINE 0xD2 回调
     */
    public static native int strMedOnline(String ystNum,String filePath);

    /**
     * 8.猫眼流媒体视频连接
     * @param window 视频连接窗口：大于等于1的值
     * @param surface 视频surface
     * @param ystNum 云视通号码
     * @param isNeedData 是否需要视频数据：1 需要；0 不需要
     * @param thumbFileName 传空字符串即可
     * @return 0：成功 -1：失败
     *
     * 回调：
     * 连接结果回调：what：CALL_CATEYE_CONNECTED  arg1:窗口   arg2:1 连接成功;2 连接失败
     * 异常断开回调：what：CALL_CATEYE_DISCONNECTED 收到此回调必须调用断开
     * 视频连接出图回调：what：CALL_NEW_PICTURE 收到此回调视频会出图，可在此时隐藏连接文字
     */
    public static native int strMedConnect(int window,Object surface,String ystNum,int isNeedData,String thumbFileName);

    /**
     * 8.猫眼流媒体视频连接
     * @param window 视频连接窗口：大于等于1的值
     * @param surface 视频surface
     * @param ystNum 云视通号码
     * @param isNeedData 是否需要视频数据：1 需要；0 不需要
     * @param thumbFileName 传空字符串即可
     * @param isAP 传false 即可
     * @param userName 传空字符串即可
     * @param password 传空字符串即可
     * @return 0：成功 -1：失败
     *
     * 回调：
     * 连接结果回调：what：CALL_CATEYE_CONNECTED  arg1:窗口   arg2:1 连接成功;2 连接失败
     * 异常断开回调：what：CALL_CATEYE_DISCONNECTED 收到此回调必须调用断开
     * 视频连接出图回调：what：CALL_NEW_PICTURE 收到此回调视频会出图，可在此时隐藏连接文字
     */
    public static native int strMedConnect(int window,Object surface,String ystNum,int isNeedData,String thumbFileName, boolean isAP, String userName, String password);


    /**
     * 9.猫眼断开视频连接
     * @param window 视频连接窗口：对应接口8的window
     */
    public static native void strMedDisconnect(int window);

    /**
     * 10.流媒体猫眼设置协议，所有设置接口均通过此方法设置，不需要先发送文本聊天请求
     * @param window 视频连接窗口：大于等于1的值
     * @param cmd
     * @param param
     * @param nPacketType
     * @param data
     * @param size
     * @return
     */
    public static native int strMedSendData(int window,int cmd,int param,byte nPacketType,byte[] data,int size);

    /**
     * 21.流媒体猫眼局域网搜索功能
     *
     * @return
     * 如果有设备，会有此回调，每次回调返回一个设备号
    //    env->CallVoidMethod(g_handle, g_notifyid, CALL_CATEYE_SEARCH_DEVICE,
    //                        (jint) is_end, (jint) 0, jmsg);
    //    is_end:1   最后一个设备
    //    is_end:0  不是最后一个设备
    //    #define CALL_CATEYE_SEARCH_DEVICE  0xD9
    //    云视通号在jmsg，values["ystno"] = pystNo; values["identify"] = pidentify;
     */
    public static native int strMedSearchDevice();


    /**
     * 22. 流媒体修改远程回放
     * @param window
     * @param enable true 暂停实时视频数据;enable： false 请求实时视频数据
     * @return
     *
     * 远程回放流程：调用strMedPlayback 暂停实时视频  ->    发送命令请求回放数据 ->
     */
    public static native boolean strMedPlayback(int window,boolean enable);


    /**
     * 13.开启手机端录像
     *
     * @param window       窗口索引
     * @param path         待保存的文件名,完整路径+文件名
     * @param enableVideo  是否录视频，true 录制  false 不录制
     * @param enableAudio  是否录音频，true 录制  false 不录制
     * @param audioCodecID 0为arm音频格式封装 1原格式封装 接口新增参数
     * @return
     */
    public static native boolean startRecord(int window, String path,
                                             boolean enableVideo, boolean enableAudio, int audioCodecID);


    /**
     * 18.对讲手机端开始录音接口
     * @param window
     * @param isStream  原对讲函数增加参数isStream，0：非流媒体 1：流媒体
     */
    public static native void recordAndsendAudioData(int window,int isStream);



}
