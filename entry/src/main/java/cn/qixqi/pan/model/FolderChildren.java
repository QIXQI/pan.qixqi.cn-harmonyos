package cn.qixqi.pan.model;

import java.util.List;

public class FolderChildren {

    private List<SimpleFolderLink> folders;
    private List<FileLink> files;

    public List<SimpleFolderLink> getFolders() {
        return folders;
    }

    public void setFolders(List<SimpleFolderLink> folders) {
        this.folders = folders;
    }

    public List<FileLink> getFiles() {
        return files;
    }

    public void setFiles(List<FileLink> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "FolderChildren{" +
                "folders=" + folders +
                ", files=" + files +
                '}';
    }
}
