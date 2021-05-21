package cn.qixqi.pan.view;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;

import java.util.ArrayList;
import java.util.List;

public class FHBottomBarItemView {

    private List<BottomBarItemInfo> bottomBarItemInfos = new ArrayList<>();

    public FHBottomBarItemView(){
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_download,
                ResourceTable.Id_bnav_download_txt,
                ResourceTable.Id_bnav_download_img,
                ResourceTable.Media_bnav_download,
                ResourceTable.Media_bnav_download_activated));
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_upload,
                ResourceTable.Id_bnav_upload_txt,
                ResourceTable.Id_bnav_upload_img,
                ResourceTable.Media_bnav_upload,
                ResourceTable.Media_bnav_upload_activated));
    }

    public List<BottomBarItemInfo> getBottomBarItemInfos(){
        return this.bottomBarItemInfos;
    }
}
