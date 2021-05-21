package cn.qixqi.pan.view;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;

import java.util.ArrayList;
import java.util.List;

public class BottomBarItemView {

    private List<BottomBarItemInfo> bottomBarItemInfos = new ArrayList<>();

    public BottomBarItemView() {
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_folder,
                ResourceTable.Id_bnav_folder_txt,
                ResourceTable.Id_bnav_folder_img,
                ResourceTable.Media_bnav_folder,
                ResourceTable.Media_bnav_folder_activated));
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_share,
                ResourceTable.Id_bnav_share_txt,
                ResourceTable.Id_bnav_share_img,
                ResourceTable.Media_bnav_share,
                ResourceTable.Media_bnav_share_activated));
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_profile,
                ResourceTable.Id_bnav_profile_txt,
                ResourceTable.Id_bnav_profile_img,
                ResourceTable.Media_bnav_profile,
                ResourceTable.Media_bnav_profile_activated));
    }

    public List<BottomBarItemInfo> getBottomBarItemInfos(){
        return this.bottomBarItemInfos;
    }
}
