package com.xiaowei.core.rx.retrofit.subscriber;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;


import com.xiaowei.core.CoreApplication;
import com.xiaowei.core.CoreApplication;
import com.xiaowei.core.rx.retrofit.exception.ApiException;
import com.xiaowei.core.rx.retrofit.exception.ExceptionEngine;
import com.xiaowei.core.utils.FileUtils;
import com.xiaowei.core.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * 下载订阅类
 *
 * @author ye.jian
 */
public abstract class DownLoadSubscribe extends Subscriber<ResponseBody> {
    private static String TAG = "DownLoadSubscribe";
    private String mSaveFilePath;
    private File mFile;

    /**
     * @param fileName 文件名称
     *                 默认存储:
     *                 有SD卡:/sdcard/Android/data/<application package>/cache
     *                 无SD卡:/data/data/<application package>/cache
     */
    public DownLoadSubscribe(@NonNull String fileName) {
        mFile = FileUtils.getFileFromCache(CoreApplication.getInstance(),
                fileName);
        mSaveFilePath = mFile.getPath();
    }

    /**
     * @param filePath 文件存储的目录
     * @param fileName 文件名称
     */
    public DownLoadSubscribe(@NonNull String filePath, @NonNull String
            fileName) {
        mSaveFilePath = filePath;
        mFile = new File(mSaveFilePath + File.separator + fileName);
    }

    @Override
    public void onCompleted() {
        _onSuccess(mFile);
    }

    @Override
    public void onError(Throwable e) {
        Logger.e(e);
        /*
          在这里(handleException)做全局的错误处理
         */
        _onError(ExceptionEngine.handleException(e));
    }

    @Override
    public void onNext(final ResponseBody responseBody) {
        //do nothing
    }

    public abstract void onProgress(double progress, long downloadByte, long
            totalByte);


    public File getFile() {
        return mFile;
    }

    // --------------------------------------------------
    // # 回调方法
    // --------------------------------------------------
    public abstract void _onSuccess(File file);

    public abstract void _onError(ApiException e);

    // --------------------------------------------------
    // #
    // --------------------------------------------------
    Handler handler = new Handler(Looper.getMainLooper());
    long fileSizeDownloaded = 0;
    long fileSize = 0;

    /**
     * 将下载的文件数据写入到硬盘
     *
     * @param body
     * @return
     */
    public boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                fileSize = body.contentLength();
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(getFile());

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(fileSizeDownloaded * 1.0f / fileSize,
                                    fileSizeDownloaded, fileSize);

                        }
                    });
                }
                outputStream.flush();

                return true;
            } catch (final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(e);
                    }
                });

                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (final IOException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onError(e);
                }
            });
            return false;
        }
    }
}