package cn.qixqi.pan.datamodel;

import cn.qixqi.pan.model.FileShare;

public class FileShareItemInfo extends FileShare {
    private boolean isChecked;

    public FileShareItemInfo() {

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public FileShareItemInfo withFileShare(FileShare fileShare){
        this.setShareId(fileShare.getShareId());
        this.setSharePass(fileShare.getSharePass());
        this.setUid(fileShare.getUid());
        this.setRootId(fileShare.getRootId());
        this.setCreateTime(fileShare.getCreateTime());
        this.setActiveTime(fileShare.getActiveTime());
        return this;
    }

    public FileShareItemInfo withChecked(boolean checked){
        this.setChecked(checked);
        return this;
    }

    @Override
    public String toString() {
        return "FileShareItemInfo{" +
                "isChecked=" + isChecked +
                "} " + super.toString();
    }
}
