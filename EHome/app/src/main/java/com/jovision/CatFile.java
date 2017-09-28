package com.jovision;

/**
 * Created by juyang on 16/12/12.
 */
public class CatFile {
    public static final int MEDIA_PICTURE = 0;
    public static final int MEDIA_VIDEO = 1;

    public static final int FILE_NORMAL = 0;
    public static final int FILE_ALARM = 1;

    private String name = "";//文件名字，也是日期名字
    private int mediaKind = -1;//媒体类型  P:图片 0          V:视频 1
    private int fileKind = -1;//图片类型   N:开头普通图片 0   A:开头报警图片 1
    private String thumbnailPath;//缩略图地址
    private String filePath;//完整图片地址
    private String fileDate;//文件时间
    private int index = -1;//顺序
    private boolean selected = false;//true：选中  false ：未选中

    public CatFile() {

    }

    public CatFile(String name, int mediaKind, int fileind, String thumbnailPath, String filePath) {

        this.name = name;
        this.mediaKind = mediaKind;
        this.fileKind = fileKind;
        this.thumbnailPath = thumbnailPath;
        this.filePath = filePath;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public int getFileKind() {
        return fileKind;
    }

    public void setFileKind(int fileKind) {
        this.fileKind = fileKind;
    }

    public int getMediaKind() {
        return mediaKind;
    }

    public void setMediaKind(int mediaKind) {
        this.mediaKind = mediaKind;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
