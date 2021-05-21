package cn.qixqi.pan.slice;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;
import cn.qixqi.pan.util.ElementUtil;
import cn.qixqi.pan.view.FHBottomBarItemView;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.utils.Color;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.List;
import java.util.stream.IntStream;

public class FileUploadAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileUploadAbilitySlice.class.getName());

    private AbilitySlice abilitySlice;

    private List<BottomBarItemInfo> bottomBarItemInfoList;

    @Override
    public void onStart(Intent intent){
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_file_upload);

        abilitySlice = this;

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        // 设置底部导航栏
        setBottomToolBar();
    }

    /**
     * 设置底部导航栏
     */
    private void setBottomToolBar(){
        FHBottomBarItemView bottomBarItemView = new FHBottomBarItemView();
        bottomBarItemInfoList = bottomBarItemView.getBottomBarItemInfos();

        IntStream.range(0, bottomBarItemInfoList.size()).forEach(position -> {
            DirectionalLayout bottomItemLayout = (DirectionalLayout) abilitySlice.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavLayoutId());
            bottomItemLayout.setVisibility(Component.VISIBLE);
            Image image = (Image) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavImgId());
            Text text = (Text) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavTextId());
            if (position == 1){
                // 设为选中
                image.setImageAndDecodeBounds(
                        bottomBarItemInfoList.get(position).getBnavActivatedImgSrcId());
                text.setTextColor(Color.BLUE);
            } else {
                image.setImageAndDecodeBounds(
                        bottomBarItemInfoList.get(position).getBnavImgSrcId());
            }

            // 设置子项点击事件
            bottomItemLayout.setClickedListener( component -> {
                unselected();
                HiLog.debug(LOG_LABEL, "设置子项点击事件：" + position);
                image.setImageAndDecodeBounds(
                        bottomBarItemInfoList.get(position).getBnavActivatedImgSrcId());
                text.setTextColor(Color.BLUE);
                startAbilityFromBnav(position);
            });
        });
    }

    /**
     * 底部导航栏实现导航功能
     * @param position
     */
    private void startAbilityFromBnav(int position){
        if (position == 0){
            present(new FileDownloadAbilitySlice(), new Intent());
        } else if (position == 1){
            return;
        }
    }

    /**
     * 底部导航栏全部子项取消选中
     */
    private void unselected(){
        IntStream.range(0, bottomBarItemInfoList.size()).forEach( position -> {
            DirectionalLayout bottomItemLayout = (DirectionalLayout) abilitySlice.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavLayoutId());
            Image image = (Image) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavImgId());
            Text text = (Text) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavTextId());
            image.setPixelMap(bottomBarItemInfoList.get(position).getBnavImgSrcId());
            text.setTextColor(Color.BLACK);
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
