package cn.qixqi.pan.view.adapter;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.FolderItemInfo;
import ohos.agp.components.*;
import ohos.app.Context;

import java.util.ArrayList;
import java.util.List;

public class FolderItemProvider extends BaseItemProvider {

    private List<FolderItemInfo> folderItemInfos;

    public FolderItemProvider(List<FolderItemInfo> folderItemInfos) {
        if (folderItemInfos != null){
            this.folderItemInfos = folderItemInfos;
        } else {
            this.folderItemInfos = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return folderItemInfos.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < folderItemInfos.size()){
            return folderItemInfos.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        Component itemComponent = component;
        FolderItemHolder holder;
        if (itemComponent == null){
            Context context = componentContainer.getContext();
            itemComponent = LayoutScatter.getInstance(context)
                    .parse(ResourceTable.Layout_folder_item, componentContainer, false);
            holder = new FolderItemHolder(itemComponent);
            itemComponent.setTag(holder);
        } else {
            holder = (FolderItemHolder) itemComponent.getTag();
        }
        holder.folderId.setText(folderItemInfos.get(position).getFolderId());
        holder.folderName.setText(folderItemInfos.get(position).getFolderName());
        if (folderItemInfos.get(position).isChecked()){
            // 文件夹被选中
            holder.folderCheck.setPixelMap(ResourceTable.Media_file_check);
        } else {
            // 文件夹未被选中
            holder.folderCheck.setPixelMap(ResourceTable.Media_file_uncheck);
        }

        return itemComponent;
    }

    /**
     * 保存子组件信息
     */
    public class FolderItemHolder{
        Text folderId;
        Text folderName;
        Image folderCheck;

        FolderItemHolder(Component component){
            folderId = (Text) component.findComponentById(ResourceTable.Id_folder_id);
            folderName = (Text) component.findComponentById(ResourceTable.Id_folder_name);
            folderCheck = (Image) component.findComponentById(ResourceTable.Id_folder_check);
        }
    }
}
