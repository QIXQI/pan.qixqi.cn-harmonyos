package cn.qixqi.pan.view;

import cn.qixqi.pan.datamodel.FolderItemInfo;
import cn.qixqi.pan.model.SimpleFolderLink;

import java.util.ArrayList;
import java.util.List;

public class FolderItemView {

    private List<FolderItemInfo> folderItemInfos = new ArrayList<>();

    public FolderItemView(List<SimpleFolderLink> simpleFolderLinks){
        if (simpleFolderLinks == null){
            return;
        }
        for (SimpleFolderLink simpleFolderLink : simpleFolderLinks){
            folderItemInfos.add(
                    new FolderItemInfo()
                            .withSimpleFolderLink(simpleFolderLink)
                            .withChecked(false));
        }
    }

    public List<FolderItemInfo> getFolderItemInfos(){
        return this.folderItemInfos;
    }
}
