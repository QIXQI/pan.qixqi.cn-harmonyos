package cn.qixqi.pan.view;

import cn.qixqi.pan.BuildConfig;
import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;

import java.util.ArrayList;
import java.util.List;

public class BottomBarFSItemView {

    private List<BottomBarItemInfo> bottomBarItemInfos = new ArrayList<>();

    public BottomBarFSItemView(){
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_item_download,
                ResourceTable.Id_bnav_item_download_txt,
                ResourceTable.Id_bnav_item_download_img,
                ResourceTable.Media_bnav_item_download,
                -1));
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_item_share,
                ResourceTable.Id_bnav_item_share_txt,
                ResourceTable.Id_bnav_item_share_img,
                ResourceTable.Media_bnav_item_share,
                -1));
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_item_delete,
                ResourceTable.Id_bnav_item_delete_txt,
                ResourceTable.Id_bnav_item_delete_img,
                ResourceTable.Media_bnav_item_delete,
                -1));
        bottomBarItemInfos.add(new BottomBarItemInfo(
                ResourceTable.Id_bnav_item_rename,
                ResourceTable.Id_bnav_item_rename_txt,
                ResourceTable.Id_bnav_item_rename_img,
                ResourceTable.Media_bnav_item_rename,
                -1));
    }

    public List<BottomBarItemInfo> getBottomBarItemInfos(){
        return this.bottomBarItemInfos;
    }
}
