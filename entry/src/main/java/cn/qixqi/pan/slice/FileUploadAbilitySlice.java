package cn.qixqi.pan.slice;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;
import cn.qixqi.pan.datamodel.FileUploadItemInfo;
import cn.qixqi.pan.model.FileUpload;
import cn.qixqi.pan.util.ElementUtil;
import cn.qixqi.pan.util.Toast;
import cn.qixqi.pan.view.FHBottomBarItemView;
import cn.qixqi.pan.view.FileUploadItemView;
import cn.qixqi.pan.view.adapter.FileUploadItemProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.IntStream;

public class FileUploadAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileUploadAbilitySlice.class.getName());

    private static final String UPLOAD_BASE_URI = "dataability:///cn.qixqi.pan.data.FileUploadDataAbility";
    private static final String UPLOAD_DATA_PATH = "/fileUpload";
    private DataAbilityHelper helper;

    private AbilitySlice abilitySlice;

    private TabList tabListUpload;

    private List<BottomBarItemInfo> bottomBarItemInfoList;

    // ListContainer 弹性回滚效果参数
    private static final int OVER_SCROLL_PERCENT = 40;
    private static final float OVER_SCROLL_RATE = 0.6f;
    private static final int REMAIN_VISIBLE_PERCENT = 20;

    private ListContainer uploadFileContainer;
    private FileUploadItemProvider fileUploadItemProvider;

    @Override
    public void onStart(Intent intent){
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_file_upload);

        abilitySlice = this;
        helper = DataAbilityHelper.creator(this);

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        initView();
        initListener();

        // 设置底部导航栏
        setBottomToolBar();

        getUploadData();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        uploadFileContainer = (ListContainer) findComponentById(ResourceTable.Id_upload_file_container);
        tabListUpload = (TabList) findComponentById(ResourceTable.Id_tab_list_upload);
        if (tabListUpload.getTabCount() == 0){
            TabList.Tab uploadedTab = tabListUpload.new Tab(getContext());
            uploadedTab.setText(ResourceTable.String_upload_tab1_text);
            tabListUpload.addTab(uploadedTab);
            TabList.Tab uploadingTab = tabListUpload.new Tab(getContext());
            uploadingTab.setText(ResourceTable.String_upload_tab2_text);
            tabListUpload.addTab(uploadingTab);
            tabListUpload.selectTabAt(0);
        }
        HiLog.debug(LOG_LABEL, String.format("tab count: %d", tabListUpload.getTabCount()));
    }

    /**
     * 初始化监听器
     */
    private void initListener(){
        tabListUpload.addTabSelectedListener(
                new TabList.TabSelectedListener() {
                    @Override
                    public void onSelected(TabList.Tab tab) {
                        // 某个 Tab 选中
                        HiLog.debug(LOG_LABEL, String.format("select tab: %s, index: %d, position: %d", tab.getText(),
                                tabListUpload.getSelectedTabIndex(), tab.getPosition()));
                        switch (tab.getPosition()){
                            case 0:
                                HiLog.debug(LOG_LABEL, "查询上传完成列表");
                                // setListContainer("上传完成");
                                FileUploadItemView uploadedItemView = new FileUploadItemView(abilitySlice, "上传完成");
                                fileUploadItemProvider.setFileUploadItemInfos(uploadedItemView.getFileUploadItemInfos());
                                fileUploadItemProvider.notifyDataChanged();
                                break;
                            case 1:
                                HiLog.debug(LOG_LABEL, "查询正在上传列表");
                                // setListContainer("正在上传");
                                FileUploadItemView uploadingItemView = new FileUploadItemView(abilitySlice, "正在上传");
                                fileUploadItemProvider.setFileUploadItemInfos(uploadingItemView.getFileUploadItemInfos());
                                fileUploadItemProvider.notifyDataChanged();
                                break;
                            default:
                                HiLog.warn(LOG_LABEL, "查询上传数据越界");
                                break;
                        }
                    }

                    @Override
                    public void onUnselected(TabList.Tab tab) {
                        // 某个 Tab 取消选中
                        HiLog.debug(LOG_LABEL, String.format("unselect tab: %s, position: %d", tab.getText(), tab.getPosition()));
                    }

                    @Override
                    public void onReselected(TabList.Tab tab) {
                        // 某个 Tab 已经选中，再次点击选中
                        HiLog.debug(LOG_LABEL, String.format("reselect tab: %s, position: %d", tab.getText(), tab.getPosition()));
                    }
                }
        );
    }

    /**
     * 获取文件上传数据
     */
    private void getUploadData(){
        switch (tabListUpload.getSelectedTabIndex()){
            case 0:
                HiLog.debug(LOG_LABEL, "查询上传完成列表");
                setListContainer("上传完成");
                break;
            case 1:
                HiLog.debug(LOG_LABEL, "查询正在上传列表");
                setListContainer("正在上传");
                break;
            default:
                HiLog.warn(LOG_LABEL, "查询上传数据越界");
                break;
        }
    }

    /**
     * 设置 ListContainer
     * @param selectUploadStatus
     */
    private void setListContainer(String selectUploadStatus){
        FileUploadItemView fileUploadItemView = new FileUploadItemView(abilitySlice, selectUploadStatus);
        fileUploadItemProvider = new FileUploadItemProvider(fileUploadItemView.getFileUploadItemInfos());

        uploadFileContainer.setItemProvider(fileUploadItemProvider);

        // 设置 ListContainer 的事件监听器
        setListClickListener();

        // 设置 ListContainer 回滚动画
        setListReboundAnimation();
    }

    /**
     * 设置 ListContainer 的事件监听器
     */
    private void setListClickListener(){
        // uploadFileContainer 子项单击事件
        uploadFileContainer.setItemClickedListener( (listContainer, component, position, id) -> {
            FileUploadItemInfo fileUploadItemInfo = (FileUploadItemInfo) fileUploadItemProvider.getItem(position);
            Toast.makeToast(abilitySlice, fileUploadItemInfo.toString(), Toast.TOAST_SHORT).show();
        });
    }

    /**
     * 设置 ListContainer 回滚动画
     */
    private void setListReboundAnimation(){
        uploadFileContainer.setReboundEffect(true);
        uploadFileContainer.setReboundEffectParams(OVER_SCROLL_PERCENT, OVER_SCROLL_RATE, REMAIN_VISIBLE_PERCENT);
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
