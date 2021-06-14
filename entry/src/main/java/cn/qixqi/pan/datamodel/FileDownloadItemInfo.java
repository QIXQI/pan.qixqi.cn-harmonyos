package cn.qixqi.pan.datamodel;

import cn.qixqi.pan.model.FileDownload;

import java.io.Serializable;

public class FileDownloadItemInfo extends FileDownload implements Serializable {

    private boolean isChecked;

    public FileDownloadItemInfo(){

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public FileDownloadItemInfo withFileDownload(FileDownload fileDownload){
        this.setDownloadId(fileDownload.getDownloadId());
        this.setLinkId(fileDownload.getLinkId());
        this.setLinkName(fileDownload.getLinkName());
        this.setFileId(fileDownload.getFileId());
        this.setFileType(fileDownload.getFileType());
        this.setFileSize(fileDownload.getFileSize());
        this.setDownloadFinishTime(fileDownload.getDownloadFinishTime());
        this.setDownloadStatus(fileDownload.getDownloadStatus());
        return this;
    }

    public FileDownloadItemInfo withChecked(boolean checked){
        this.setChecked(checked);
        return this;
    }

    @Override
    public String toString() {
        return "FileDownloadItemInfo{" +
                "isChecked=" + isChecked +
                "} " + super.toString();
    }
}
