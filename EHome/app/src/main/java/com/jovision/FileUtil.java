package com.jovision;

import java.io.File;

/**
 * Created by Administrator on 2016/11/15.
 */

public class FileUtil {
    /**
     * 递归创建文件目录
     *
     * @param file 要创建的文件
     * @author
     */
    public static void createDirectory(File file) {
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
            createDirectory(file.getParentFile());
            boolean res = file.mkdir();
            if (!res) {
                file.delete();
            }
        }
    }
}
