package com.xiaowei.core.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLog {

    private static final String V = "V";
    private static final String I = "I";
    private static final String E = "e";
    private static final String Ex = "E";

    private static final String RW = "rw";
    private static final String TAG = "SimpleLog";
    private static final String LINE_SEPARATOR = "\n";
    private static final String FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS";

    private static String FOLDER = null;

    private static boolean ENABLE_FILE = true;
    private static boolean ENABLE_LOGCAT = true;

    private static File checkTag(String tag) {
        File result = null;

        if (null != FOLDER && init(FOLDER)) {
            result = new File(FOLDER + File.separator + tag);
            if (!result.exists()) {
                try {
                    result.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    @SuppressLint("SimpleDateFormat")
    private static String prepare(String level, String msg) {
        StringBuilder sBuilder = new StringBuilder(msg.length() + 64);
        sBuilder.append(new SimpleDateFormat(FORMATTER).format(new Date()))
                .append(File.pathSeparatorChar);

        if (null != level) {
            sBuilder.append(level).append(File.separatorChar);
        }

        sBuilder.append(msg);
        sBuilder.append(LINE_SEPARATOR);
        return sBuilder.toString();
    }

    private static synchronized boolean append(File file, String string) {
        boolean result = false;

        if (null != file) {
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(file.getPath(), RW);
                randomAccessFile.seek(randomAccessFile.length());
                randomAccessFile.writeBytes(new String(string.getBytes
                        ("UTF-8"), "iso-8859-1"));
                randomAccessFile.close();
                randomAccessFile = null;
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException e2) {
                    } finally {
                        randomAccessFile = null;
                    }
                }

                if (!result) {
                   Log.e(TAG, "append failed when write");
                    ENABLE_FILE = false;
                }
            }
        } else {
           Log.e(TAG, "append without file");
        }

        return result;
    }

    public static void v(String tag, String msg) {
        if (msg == null) {
            msg = "NULL";
        }

        if (ENABLE_LOGCAT) {
            Log.v(tag, msg);
        }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(V, msg));
        }
    }

    public static void i(String tag, String msg) {
        if (msg == null) {
            msg = "NULL";
        }

        if (ENABLE_LOGCAT) {
            Log.i(tag, msg);
        }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(I, msg));
        }
    }

    public static void e(String tag, String msg) {
        if (msg == null) {
            msg = "NULL";
        }

        if (ENABLE_LOGCAT) {
           Log.e(tag, msg);
        }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(E, msg));
        }
    }

    public static void e(String tag, Exception e) {
        StringBuilder sBuilder = new StringBuilder(16 * 1024);
        sBuilder.append(e.getMessage()).append(LINE_SEPARATOR);

        StackTraceElement[] elements = e.getStackTrace();
        int size = elements.length;

        for (int i = 0; i < size; i++) {
            sBuilder.append(elements[i].getClassName()).append(".")
                    .append(elements[i].getMethodName()).append("@")
                    .append(elements[i].getLineNumber()).append(LINE_SEPARATOR);
        }

        String msg = sBuilder.toString();

        if (ENABLE_LOGCAT) {
           Log.e(tag, msg);
        }

        if (ENABLE_FILE) {
            append(checkTag(tag), prepare(Ex, msg));
        }
    }

    /**
     * set log files' folder
     *
     * @param logPath
     * @return
     */
    public static boolean init(String logPath) {
        boolean result;
        File folder = new File(logPath);
        if (!folder.exists()) {
            result = folder.mkdirs();
        } else {
            result = true;
        }

        if (result) {
            result = false;
            if (folder.canRead() && folder.canWrite() && folder.isDirectory()) {
                FOLDER = logPath;
                result = true;
            }
        }

        return result;
    }

    public static void enableFile(boolean enable) {
        ENABLE_FILE = enable;
    }

    public static void enableLogcat(boolean enable) {
        ENABLE_LOGCAT = enable;
    }

}
