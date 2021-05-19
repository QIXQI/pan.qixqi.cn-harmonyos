package cn.qixqi.pan.model;

import java.util.Date;

public class FolderLink {

    private String folderId;
    private String folderName;
    private String uid;
    private String parent;
    private Date createTime;
    private FolderChildren children;

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public FolderChildren getChildren() {
        return children;
    }

    public void setChildren(FolderChildren children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "FolderLink{" +
                "folderId='" + folderId + '\'' +
                ", folderName='" + folderName + '\'' +
                ", uid='" + uid + '\'' +
                ", parent='" + parent + '\'' +
                ", createTime=" + createTime +
                ", children=" + children +
                '}';
    }
}
