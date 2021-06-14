package cn.qixqi.pan.model;

public class FileUpload {

    private int uploadId;
    private String linkId;
    private String linkName;
    private String fileId;
    private String fileType;
    private long fileSize;
    private long uploadFinishTime;
    private String uploadStatus;

    public FileUpload(){
        super();
    }

    public FileUpload(int uploadId, String linkId, String linkName, String fileId, String fileType,
                      long fileSize, long uploadFinishTime, String uploadStatus) {
        this.uploadId = uploadId;
        this.linkId = linkId;
        this.linkName = linkName;
        this.fileId = fileId;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadFinishTime = uploadFinishTime;
        this.uploadStatus = uploadStatus;
    }

    public int getUploadId() {
        return uploadId;
    }

    public void setUploadId(int uploadId) {
        this.uploadId = uploadId;
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

    public long getUploadFinishTime() {
        return uploadFinishTime;
    }

    public void setUploadFinishTime(long uploadFinishTime) {
        this.uploadFinishTime = uploadFinishTime;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    @Override
    public String toString() {
        return "FileUpload{" +
                "uploadId=" + uploadId +
                ", linkId='" + linkId + '\'' +
                ", linkName='" + linkName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", uploadFinishTime=" + uploadFinishTime +
                ", uploadStatus='" + uploadStatus + '\'' +
                '}';
    }
}
