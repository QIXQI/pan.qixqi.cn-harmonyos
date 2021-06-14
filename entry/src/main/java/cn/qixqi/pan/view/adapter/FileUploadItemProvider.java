package cn.qixqi.pan.view.adapter;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.FileUploadItemInfo;
import ohos.agp.components.*;
import ohos.app.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUploadItemProvider extends BaseItemProvider {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
    private List<FileUploadItemInfo> fileUploadItemInfos;

    public FileUploadItemProvider(List<FileUploadItemInfo> fileUploadItemInfos){
        if (fileUploadItemInfos != null){
            this.fileUploadItemInfos = fileUploadItemInfos;
        } else {
            this.fileUploadItemInfos = new ArrayList<>();
        }
    }

    public void setFileUploadItemInfos(List<FileUploadItemInfo> fileUploadItemInfos){
        if (fileUploadItemInfos != null){
            this.fileUploadItemInfos = fileUploadItemInfos;
        }
    }

    @Override
    public int getCount(){
        return this.fileUploadItemInfos.size();
    }

    @Override
    public Object getItem(int position){
        if (position >= 0 && position < this.fileUploadItemInfos.size()){
            return this.fileUploadItemInfos.get(position);
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
        FileUploadItemHolder holder;
        if (itemComponent == null){
            Context context = componentContainer.getContext();
            itemComponent = LayoutScatter.getInstance(context)
                    .parse(ResourceTable.Layout_upload_file_item, componentContainer, false);
            holder = new FileUploadItemHolder(itemComponent);
            itemComponent.setTag(holder);
        }  else {
            holder = (FileUploadItemHolder) itemComponent.getTag();
        }
        // [TODO] 更改 uploadImg 图像
        holder.uploadImg.setPixelMap(ResourceTable.Media_file_unknown);
        holder.linkName.setText(fileUploadItemInfos.get(position).getLinkName());
        holder.uploadFinishTime.setText(simpleDateFormat.format(fileUploadItemInfos.get(position).getUploadFinishTime()));
        holder.uploadStatus.setText(fileUploadItemInfos.get(position).getUploadStatus());
        if (fileUploadItemInfos.get(position).isChecked()){
            // 选中
            holder.uploadCheck.setPixelMap(ResourceTable.Media_file_check);
        } else {
            // 未选中
            holder.uploadCheck.setPixelMap(ResourceTable.Media_file_uncheck);
        }
        return itemComponent;
    }

    public class FileUploadItemHolder{
        Image uploadImg;
        Text linkName;
        Text uploadFinishTime;
        Text uploadStatus;
        Image uploadCheck;

        FileUploadItemHolder(Component component){
            uploadImg = (Image) component.findComponentById(ResourceTable.Id_upload_img);
            linkName = (Text) component.findComponentById(ResourceTable.Id_link_name);
            uploadFinishTime = (Text) component.findComponentById(ResourceTable.Id_upload_finish_time);
            uploadStatus = (Text) component.findComponentById(ResourceTable.Id_upload_status);
            uploadCheck = (Image) component.findComponentById(ResourceTable.Id_upload_check);
        }
    }
}
