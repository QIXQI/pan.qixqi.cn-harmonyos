package cn.qixqi.pan.view.adapter;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.FileItemInfo;
import cn.qixqi.pan.util.TimeUtil;
import ohos.agp.components.*;
import ohos.app.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileItemProvider extends BaseItemProvider {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
    private List<FileItemInfo> fileItemInfos;

    public FileItemProvider(List<FileItemInfo> fileItemInfos) {
        if (fileItemInfos != null){
            this.fileItemInfos = fileItemInfos;
        } else {
            this.fileItemInfos = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return fileItemInfos.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < fileItemInfos.size()){
            return fileItemInfos.get(position);
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
        FileItemHolder holder;
        if (itemComponent == null){
            Context context = componentContainer.getContext();
            itemComponent = LayoutScatter.getInstance(context)
                    .parse(ResourceTable.Layout_file_item, componentContainer, false);
            holder = new FileItemHolder(itemComponent);
            itemComponent.setTag(holder);
        } else {
            holder = (FileItemHolder) itemComponent.getTag();
        }
        // [TODO] 更改 fileImg 图像
        holder.fileImg.setPixelMap(ResourceTable.Media_file_unknown);
        holder.fileId.setText(fileItemInfos.get(position).getFileId());
        holder.linkId.setText(fileItemInfos.get(position).getLinkId());
        holder.linkName.setText(fileItemInfos.get(position).getLinkName());
        holder.linkCreateTime.setText(simpleDateFormat.format(fileItemInfos.get(position).getCreateTime()));
        // [TODO] 更改文件大小格式
        holder.fileSize.setText(TimeUtil.getFormatSize(fileItemInfos.get(position).getFileSize()));
        if (fileItemInfos.get(position).isChecked()){
            // 文件被选中
            holder.fileCheck.setPixelMap(ResourceTable.Media_file_check);
        } else {
            // 文件未被选中
            holder.fileCheck.setPixelMap(ResourceTable.Media_file_uncheck);
        }

        return itemComponent;
    }

    /**
     * 保存子组件信息
     */
    public class FileItemHolder{
        Image fileImg;
        Text fileId;
        Text linkId;
        Text linkName;
        Text linkCreateTime;
        Text fileSize;
        Image fileCheck;

        FileItemHolder(Component component){
            fileImg = (Image) component.findComponentById(ResourceTable.Id_file_img);
            fileId = (Text) component.findComponentById(ResourceTable.Id_file_id);
            linkId = (Text) component.findComponentById(ResourceTable.Id_link_id);
            linkName = (Text) component.findComponentById(ResourceTable.Id_link_name);
            linkCreateTime = (Text) component.findComponentById(ResourceTable.Id_link_create_time);
            fileSize = (Text) component.findComponentById(ResourceTable.Id_file_size);
            fileCheck = (Image) component.findComponentById(ResourceTable.Id_file_check);
        }
    }
}
