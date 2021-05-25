package cn.qixqi.pan.model;


public class FileDownload {

    private int downloadId;
    private String linkId;
    private String linkName;
    private String fileId;
    private String fileType;
    private long fileSize;
    private long downloadFinishTime;
    private String downloadStatus;

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

    public long getDownloadFinishTime() {
        return downloadFinishTime;
    }

    public void setDownloadFinishTime(long downloadFinishTime) {
        this.downloadFinishTime = downloadFinishTime;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    @Override
    public String toString() {
        return "FileDownload{" +
                "linkId='" + linkId + '\'' +
                ", linkName='" + linkName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", downloadFinishTime=" + downloadFinishTime +
                ", downloadStatus='" + downloadStatus + '\'' +
                '}';
    }
}
