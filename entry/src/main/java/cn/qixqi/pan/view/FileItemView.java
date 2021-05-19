package cn.qixqi.pan.view;

import cn.qixqi.pan.datamodel.FileItemInfo;
import cn.qixqi.pan.model.FileLink;

import java.util.ArrayList;
import java.util.List;

public class FileItemView {

    private List<FileItemInfo> fileItemInfos = new ArrayList<>();

    public FileItemView(List<FileLink> fileLinks) {
        if (fileLinks == null){
            return;
        }
        for(FileLink fileLink : fileLinks){
            fileItemInfos.add(
                    new FileItemInfo()
                            .withFileLink(fileLink)
                            .withChecked(false));
        }
    }

    public List<FileItemInfo> getFileItemInfos(){
        return this.fileItemInfos;
    }
}
