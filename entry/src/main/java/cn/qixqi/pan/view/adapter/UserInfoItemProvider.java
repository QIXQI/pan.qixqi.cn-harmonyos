package cn.qixqi.pan.view.adapter;

import cn.qixqi.pan.MyApplication;
import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.UserInfoItemInfo;
import ohos.agp.components.*;
import ohos.app.Context;

import java.util.ArrayList;
import java.util.List;

public class UserInfoItemProvider extends BaseItemProvider {

    private List<UserInfoItemInfo> userInfoItemInfos;
    private Context context;

    public UserInfoItemProvider(List<UserInfoItemInfo> userInfoItemInfos, Context context){
        if (userInfoItemInfos != null){
            this.userInfoItemInfos = userInfoItemInfos;
        } else {
            this.userInfoItemInfos = new ArrayList<>();
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        return userInfoItemInfos.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < userInfoItemInfos.size()){
            return userInfoItemInfos.get(position);
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
        UserInfoItemHolder holder;
        if (itemComponent == null){
            Context context = componentContainer.getContext();
            itemComponent = LayoutScatter.getInstance(context)
                    .parse(ResourceTable.Layout_user_info_item, componentContainer, false);
            holder = new UserInfoItemHolder(itemComponent);
            itemComponent.setTag(holder);
        } else {
            holder = (UserInfoItemHolder) itemComponent.getTag();
        }
        if (userInfoItemInfos.get(position).getValue() != null){
            holder.valueText.setText(userInfoItemInfos.get(position).getValue());
        } else {
            holder.valueText.setText("空");
        }
        holder.keyText.setText(context.getString(
                userInfoItemInfos.get(position).getKeyId()));
        holder.editImg.setPixelMap(userInfoItemInfos.get(position).getEditImgId());

        return itemComponent;
    }

    /**
     * 保存子组件信息
     */
    public class UserInfoItemHolder{
        Text valueText;
        Text keyText;
        Image editImg;

        UserInfoItemHolder(Component component){
            valueText = (Text) component.findComponentById(ResourceTable.Id_valueText);
            keyText = (Text) component.findComponentById(ResourceTable.Id_keyText);
            editImg = (Image) component.findComponentById(ResourceTable.Id_user_info_edit_img);
        }
    }
}
