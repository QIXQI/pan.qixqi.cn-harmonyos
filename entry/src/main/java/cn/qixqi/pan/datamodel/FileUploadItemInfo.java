package cn.qixqi.pan.datamodel;

import cn.qixqi.pan.model.FileUpload;

public class FileUploadItemInfo extends FileUpload {

    private boolean isChecked;

    public FileUploadItemInfo(){

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public FileUploadItemInfo withFileUpload(FileUpload fileUpload){
        this.setUploadId(fileUpload.getUploadId());
        this.setLinkId(fileUpload.getLinkId());
        this.setLinkName(fileUpload.getLinkName());
        this.setFileId(fileUpload.getFileId());
        this.setFileType(fileUpload.getFileType());
        this.setFileSize(fileUpload.getFileSize());
        this.setUploadFinishTime(fileUpload.getUploadFinishTime());
        this.setUploadStatus(fileUpload.getUploadStatus());
        return this;
    }

    public FileUploadItemInfo withChecked(boolean checked){
        this.setChecked(checked);
        return this;
    }

    @Override
    public String toString() {
        return "FileUploadItemInfo{" +
                "isChecked=" + isChecked +
                "} " + super.toString();
    }
}
