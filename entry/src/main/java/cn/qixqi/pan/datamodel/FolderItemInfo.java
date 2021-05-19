package cn.qixqi.pan.datamodel;

import cn.qixqi.pan.model.SimpleFolderLink;

public class FolderItemInfo extends SimpleFolderLink {

    private boolean isChecked;

    public FolderItemInfo(){

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public FolderItemInfo withSimpleFolderLink(SimpleFolderLink simpleFolderLink){
        this.setFolderId(simpleFolderLink.getFolderId());
        this.setFolderName(simpleFolderLink.getFolderName());
        return this;
    }

    public FolderItemInfo withChecked(boolean checked){
        this.setChecked(checked);
        return this;
    }

    @Override
    public String toString() {
        return "FolderItemInfo{" +
                "isChecked=" + isChecked +
                "} " + super.toString();
    }
}
