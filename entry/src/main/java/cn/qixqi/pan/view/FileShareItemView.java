package cn.qixqi.pan.view;

import cn.qixqi.pan.datamodel.FileShareItemInfo;
import cn.qixqi.pan.model.FileShare;

import java.util.ArrayList;
import java.util.List;

public class FileShareItemView {

    private List<FileShareItemInfo> fileShareItemInfos = new ArrayList<>();

    public FileShareItemView(List<FileShare> fileShares){
        if (fileShares == null){
            return;
        }
        for (FileShare fileShare : fileShares){
            fileShareItemInfos.add(
                    new FileShareItemInfo()
                            .withFileShare(fileShare)
                            .withChecked(false));
        }
    }

    public List<FileShareItemInfo> getFileShareItemInfos(){
        return this.fileShareItemInfos;
    }
}
