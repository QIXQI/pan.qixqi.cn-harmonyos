package cn.qixqi.pan.view.adapter;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.FileShareItemInfo;
import ohos.agp.components.*;
import ohos.app.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileShareItemProvider extends BaseItemProvider {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
    private List<FileShareItemInfo> fileShareItemInfos;

    public FileShareItemProvider(List<FileShareItemInfo> fileShareItemInfos) {
        if (fileShareItemInfos != null){
            this.fileShareItemInfos = fileShareItemInfos;
        } else {
            this.fileShareItemInfos = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return fileShareItemInfos.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < fileShareItemInfos.size()){
            return fileShareItemInfos.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer){
        Component itemComponent = component;
        FileShareItemHolder holder;
        if (itemComponent == null){
            Context context = componentContainer.getContext();
            itemComponent = LayoutScatter.getInstance(context)
                    .parse(ResourceTable.Layout_file_share_item, componentContainer, false);
            holder = new FileShareItemHolder(itemComponent);
            itemComponent.setTag(holder);
        } else {
            holder = (FileShareItemHolder) itemComponent.getTag();
        }
        holder.fileShareImg.setPixelMap(ResourceTable.Media_file_share);
        holder.shareId.setText(fileShareItemInfos.get(position).getShareId().substring(0, 13));
        holder.shareCreateTime.setText(
                simpleDateFormat.format(fileShareItemInfos.get(position).getCreateTime()));
        holder.fileShareActiveTime.setText(String.format("%d天有效",
                fileShareItemInfos.get(position).getActiveTime()));
        if (fileShareItemInfos.get(position).isChecked()){
            // 文件分享被选中
            holder.fileShareCheck.setPixelMap(ResourceTable.Media_file_check);
        } else {
            // 文件分享未被选中
            holder.fileShareCheck.setPixelMap(ResourceTable.Media_file_uncheck);
        }

        return itemComponent;
    }

    /**
     * 保存子组件信息
     */
    public class FileShareItemHolder{
        Image fileShareImg;
        Text shareId;
        Text shareCreateTime;
        Text fileShareActiveTime;
        Image fileShareCheck;

        FileShareItemHolder(Component component){
            fileShareImg = (Image) component.findComponentById(ResourceTable.Id_file_share_img);
            shareId = (Text) component.findComponentById(ResourceTable.Id_share_id);
            shareCreateTime = (Text) component.findComponentById(ResourceTable.Id_share_create_time);
            fileShareActiveTime = (Text) component.findComponentById(ResourceTable.Id_file_share_active_time);
            fileShareCheck = (Image) component.findComponentById(ResourceTable.Id_file_share_check);
        }
    }
}
