package cn.qixqi.pan.model;

import java.util.Date;

public class FileLink {

    private String linkId;
    private String linkName;
    private String fileId;
    private String fileType;
    private long fileSize;
    private Date createTime;

    public FileLink(){

    }

    public FileLink(FileLink fileLink){
        this.linkId = fileLink.getLinkId();
        this.linkName = fileLink.getLinkName();
        this.fileId = fileLink.getFileId();
        this.fileType = fileLink.getFileType();
        this.fileSize = fileLink.getFileSize();
        this.createTime = fileLink.getCreateTime();
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "FileLink{" +
                "linkId='" + linkId + '\'' +
                ", linkName='" + linkName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", createTime=" + createTime +
                '}';
    }
}
