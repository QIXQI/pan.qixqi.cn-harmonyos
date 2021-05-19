package cn.qixqi.pan.datamodel;

import cn.qixqi.pan.model.FileLink;

public class FileItemInfo extends FileLink {

    private boolean isChecked;

    public FileItemInfo(){

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public FileItemInfo withFileLink(FileLink fileLink){
        this.setLinkId(fileLink.getLinkId());
        this.setLinkName(fileLink.getLinkName());
        this.setFileId(fileLink.getFileId());
        this.setFileType(fileLink.getFileType());
        this.setFileSize(fileLink.getFileSize());
        this.setCreateTime(fileLink.getCreateTime());
        return this;
    }

    public FileItemInfo withChecked(boolean checked){
        this.setChecked(checked);
        return this;
    }

    @Override
    public String toString() {
        return "FileItemInfo{" +
                "isChecked=" + isChecked +
                "} " + super.toString();
    }
}
