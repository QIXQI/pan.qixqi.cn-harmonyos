package cn.qixqi.pan.model;

public class SimpleFolderLink {

    private String folderId;
    private String folderName;

    public SimpleFolderLink(){

    }

    public SimpleFolderLink(SimpleFolderLink simpleFolderLink){
        this.folderId = simpleFolderLink.getFolderId();
        this.folderName = simpleFolderLink.getFolderName();
    }

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

    @Override
    public String toString() {
        return "SimpleFolderLink{" +
                "folderId='" + folderId + '\'' +
                ", folderName='" + folderName + '\'' +
                '}';
    }
}
