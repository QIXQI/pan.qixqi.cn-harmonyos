package cn.qixqi.pan.model;

public class FileShareLink {

    private String folderId;
    private String shareId;
    private String folderName;
    private String parent;
    private FolderChildren children;

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public FolderChildren getChildren() {
        return children;
    }

    public void setChildren(FolderChildren children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "FileShareLink{" +
                "folderId='" + folderId + '\'' +
                ", shareId='" + shareId + '\'' +
                ", folderName='" + folderName + '\'' +
                ", parent='" + parent + '\'' +
                ", children=" + children +
                '}';
    }
}
