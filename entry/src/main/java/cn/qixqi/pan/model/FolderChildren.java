package cn.qixqi.pan.model;

import java.util.ArrayList;
import java.util.List;

public class FolderChildren {

    private List<SimpleFolderLink> folders;
    private List<FileLink> files;

    public FolderChildren(){
        folders = new ArrayList<>();
        files = new ArrayList<>();
    }

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

    public void addFolder(SimpleFolderLink simpleFolderLink){
        this.folders.add(simpleFolderLink);
    }

    public void addFile(FileLink fileLink){
        this.files.add(fileLink);
    }

    @Override
    public String toString() {
        return "FolderChildren{" +
                "folders=" + folders +
                ", files=" + files +
                '}';
    }
}
