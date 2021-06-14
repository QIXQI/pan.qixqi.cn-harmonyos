package cn.qixqi.pan.view.adapter;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.FileDownloadItemInfo;
import ohos.agp.components.*;
import ohos.app.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileDownloadItemProvider extends BaseItemProvider {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
    private List<FileDownloadItemInfo> fileDownloadItemInfos;

    public FileDownloadItemProvider(List<FileDownloadItemInfo> fileDownloadItemInfos){
        if (fileDownloadItemInfos != null){
            this.fileDownloadItemInfos = fileDownloadItemInfos;
        } else {
            this.fileDownloadItemInfos = new ArrayList<>();
        }
    }

    public void setFileDownloadItemInfos(List<FileDownloadItemInfo> fileDownloadItemInfos){
        if (fileDownloadItemInfos != null){
            this.fileDownloadItemInfos = fileDownloadItemInfos;
        }
    }

    @Override
    public int getCount(){
        return this.fileDownloadItemInfos.size();
    }

    @Override
    public Object getItem(int position){
        if (position >= 0 && position < this.fileDownloadItemInfos.size()){
            return this.fileDownloadItemInfos.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer){
        Component itemComponent = component;
        FileDownloadItemHolder holder;
        if (itemComponent == null){
            Context context = componentContainer.getContext();
            itemComponent = LayoutScatter.getInstance(context)
                    .parse(ResourceTable.Layout_download_file_item, componentContainer, false);
            holder = new FileDownloadItemHolder(itemComponent);
            itemComponent.setTag(holder);
        } else {
            holder = (FileDownloadItemHolder) itemComponent.getTag();
        }
        // [TODO] 更改 downloadImg 图像
        holder.downloadImg.setPixelMap(ResourceTable.Media_file_unknown);
        holder.linkName.setText(fileDownloadItemInfos.get(position).getLinkName());
        holder.downloadFinishTime.setText(simpleDateFormat.format(fileDownloadItemInfos.get(position).getDownloadFinishTime()));
        holder.downloadStatus.setText(fileDownloadItemInfos.get(position).getDownloadStatus());
        if (fileDownloadItemInfos.get(position).isChecked()){
            // 选中
            holder.downloadCheck.setPixelMap(ResourceTable.Media_file_check);
        } else {
            // 未选中
            holder.downloadCheck.setPixelMap(ResourceTable.Media_file_uncheck);
        }
        return itemComponent;
    }

    public class FileDownloadItemHolder{
        Image downloadImg;
        Text linkName;
        Text downloadFinishTime;
        Text downloadStatus;
        Image downloadCheck;

        FileDownloadItemHolder(Component component){
            downloadImg = (Image) component.findComponentById(ResourceTable.Id_download_img);
            linkName = (Text) component.findComponentById(ResourceTable.Id_link_name);
            downloadFinishTime = (Text) component.findComponentById(ResourceTable.Id_download_finish_time);
            downloadStatus = (Text) component.findComponentById(ResourceTable.Id_download_status);
            downloadCheck = (Image) component.findComponentById(ResourceTable.Id_download_check);
        }
    }

}
