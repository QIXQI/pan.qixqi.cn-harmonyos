package cn.qixqi.pan.view.adapter;

import cn.qixqi.pan.MyApplication;
import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.FileItemInfo;
import cn.qixqi.pan.datamodel.FolderItemInfo;
import cn.qixqi.pan.model.FolderChildren;
import cn.qixqi.pan.util.TimeUtil;
import cn.qixqi.pan.util.Toast;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChildItemProvider extends BaseItemProvider implements Component.ClickedListener {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, ChildItemProvider.class.getName());

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
    private List<FolderItemInfo> folderItemInfos;
    private List<FileItemInfo> fileItemInfos;
    private Callback callback;

    /**
     * 自定义接口，回调按钮点击事件到 FileSystemAbilitySlice
     */
    public interface Callback{
        void click(Component component);
    }

    public ChildItemProvider(List<FolderItemInfo> folderItemInfos, List<FileItemInfo> fileItemInfos, Callback callback){
        if (folderItemInfos != null){
            this.folderItemInfos = folderItemInfos;
        } else {
            this.folderItemInfos = new ArrayList<>();
        }

        if (fileItemInfos != null){
            this.fileItemInfos = fileItemInfos;
        } else {
            this.fileItemInfos = new ArrayList<>();
        }
        this.callback = callback;
    }

    public boolean isFolder(int position){
        if (position < folderItemInfos.size() && position >= 0){
            return true;
        } else {
            return false;
        }
    }

    public void cancelSelected(){
        for (FolderItemInfo folderItemInfo : folderItemInfos){
            folderItemInfo.setChecked(false);
        }
        for (FileItemInfo fileItemInfo : fileItemInfos){
            fileItemInfo.setChecked(false);
        }
    }

    public FolderChildren getSelectedChildren(){
        FolderChildren children = new FolderChildren();
        for (FolderItemInfo folderItemInfo : folderItemInfos){
            if (folderItemInfo.isChecked()){
                children.addFolder(folderItemInfo);
            }
        }
        for (FileItemInfo fileItemInfo : fileItemInfos){
            if (fileItemInfo.isChecked()){
                children.addFile(fileItemInfo);
            }
        }
        return children;
    }

    @Override
    public int getCount() {
        return folderItemInfos.size() + fileItemInfos.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0){
            return null;
        } else if (position < folderItemInfos.size()){
            // 文件夹
            return folderItemInfos.get(position);
        } else if (position < folderItemInfos.size() + fileItemInfos.size()){
            // 文件
            return fileItemInfos.get(position - folderItemInfos.size());
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
        HiLog.debug(LOG_LABEL, String.format("count: %d, position: %d", getCount(), position));
        Component itemComponent = component;
        if (position < 0){
            return null;
        } else if (position < folderItemInfos.size()){
            // 文件夹
            FolderItemHolder holder;
            if (itemComponent == null || !(itemComponent.getTag() instanceof FolderItemHolder)){
                // itemComponent 为空，或不是文件夹组件则新建
                Context context = componentContainer.getContext();
                itemComponent = LayoutScatter.getInstance(context)
                        .parse(ResourceTable.Layout_folder_item, componentContainer, false);
                holder = new FolderItemHolder(itemComponent);
                itemComponent.setTag(holder);
            } else {
                holder = (FolderItemHolder) itemComponent.getTag();
            }
            // 设置 check_layout
            itemComponent.findComponentById(ResourceTable.Id_check_layout).setClickedListener(this);
            itemComponent.findComponentById(ResourceTable.Id_check_layout).setTag(position);
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
        } else if (position < folderItemInfos.size() + fileItemInfos.size()){
            HiLog.debug(LOG_LABEL, String.format("position: %d", position));
            // 文件
            // 文件链接列表真实下标
            int i = position - folderItemInfos.size();
            FileItemHolder holder;
            if (itemComponent == null || !(itemComponent.getTag() instanceof FileItemHolder)){
                // itemComponent 为空，或不是文件组件则新建
                Context context = componentContainer.getContext();
                itemComponent = LayoutScatter.getInstance(context)
                        .parse(ResourceTable.Layout_file_item, componentContainer, false);
                holder = new FileItemHolder(itemComponent);
                itemComponent.setTag(holder);
            } else {
                holder = (FileItemHolder) itemComponent.getTag();
            }
            // 设置 check_layout
            itemComponent.findComponentById(ResourceTable.Id_check_layout).setClickedListener(this);
            itemComponent.findComponentById(ResourceTable.Id_check_layout).setTag(position);
            HiLog.debug(LOG_LABEL, String.format("holder: %s", holder.toString()));
            // [TODO] 更改 fileImg 图像
            holder.fileImg.setPixelMap(ResourceTable.Media_file_unknown);
            holder.fileId.setText(fileItemInfos.get(i).getFileId());
            holder.linkId.setText(fileItemInfos.get(i).getLinkId());
            holder.linkName.setText(fileItemInfos.get(i).getLinkName());
            holder.linkCreateTime.setText(simpleDateFormat.format(fileItemInfos.get(i).getCreateTime()));
            // [TODO] 更改文件大小格式
            holder.fileSize.setText(TimeUtil.getFormatSize(fileItemInfos.get(i).getFileSize()));
            if (fileItemInfos.get(i).isChecked()){
                // 文件被选中
                holder.fileCheck.setPixelMap(ResourceTable.Media_file_check);
            } else {
                // 文件未被选中
                holder.fileCheck.setPixelMap(ResourceTable.Media_file_uncheck);
            }
            return itemComponent;
        } else {
            return null;
        }
    }

    /**
     * 保存子文件夹组件信息
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

    /**
     * 保存子文件组件信息
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

    /**
     * 点击事件，调用自定义接口
     * @param component
     */
    @Override
    public void onClick(Component component) {
        // Toast.makeToast(MyApplication.getAppContext(), "provider click", Toast.TOAST_SHORT).show();
        callback.click(component);
    }
}
