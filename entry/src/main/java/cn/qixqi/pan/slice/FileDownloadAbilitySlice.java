package cn.qixqi.pan.slice;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;
import cn.qixqi.pan.datamodel.FileDownloadItemInfo;
import cn.qixqi.pan.util.ElementUtil;
import cn.qixqi.pan.util.Toast;
import cn.qixqi.pan.view.BottomBarItemView;
import cn.qixqi.pan.view.FHBottomBarItemView;
import cn.qixqi.pan.view.FileDownloadItemView;
import cn.qixqi.pan.view.adapter.FileDownloadItemProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.Debug;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;

import java.util.List;
import java.util.stream.IntStream;

public class FileDownloadAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileDownloadAbilitySlice.class.getName());

    private static final String DOWNLOAD_BASE_URI = "dataability:///cn.qixqi.pan.data.FileDownloadDataAbility";
    private static final String DOWNLOAD_DATA_PATH = "/fileDownload";
    private static final String FILE_BASE_URI = "dataability:///cn.qixqi.pan.data.FileDataAbility";
    private static final String FILE_DATA_PATH = "/download";
    private DataAbilityHelper helper;

    private AbilitySlice abilitySlice;

    private TabList tabListDownload;

    private List<BottomBarItemInfo> bottomBarItemInfoList;

    // ListContainer 弹性回滚效果参数
    private static final int OVER_SCROLL_PERCENT = 40;
    private static final float OVER_SCROLL_RATE = 0.6f;
    private static final int REMAIN_VISIBLE_PERCENT = 20;

    private ListContainer downloadFileContainer;
    private FileDownloadItemProvider fileDownloadItemProvider;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_file_download);

        abilitySlice = this;
        helper = DataAbilityHelper.creator(this);

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        initView();
        initListener();

        // 设置底部导航栏
        setBottomToolBar();

        getDownloadData();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        downloadFileContainer = (ListContainer) findComponentById(ResourceTable.Id_download_file_container);
        tabListDownload = (TabList) findComponentById(ResourceTable.Id_tab_list_download);
        if (tabListDownload.getTabCount() == 0){
            TabList.Tab downloadedTab = tabListDownload.new Tab(getContext());
            downloadedTab.setText(ResourceTable.String_download_tab1_text);
            tabListDownload.addTab(downloadedTab);
            TabList.Tab downloadingTab = tabListDownload.new Tab(getContext());
            downloadingTab.setText(ResourceTable.String_download_tab2_text);
            tabListDownload.addTab(downloadingTab);
            tabListDownload.selectTabAt(0);
        }
        HiLog.debug(LOG_LABEL, String.format("tab count: %d", tabListDownload.getTabCount()));
    }

    /**
     * 初始化监听器
     */
    private void initListener(){
        tabListDownload.addTabSelectedListener(
                new TabList.TabSelectedListener(){

                    @Override
                    public void onSelected(TabList.Tab tab) {
                        // 某个 Tab 选中
                        HiLog.debug(LOG_LABEL, String.format("select tab: %s, index: %d, position: %d", tab.getText(),
                                tabListDownload.getSelectedTabIndex(), tab.getPosition()));
                        switch (tab.getPosition()){
                            case 0:
                                HiLog.debug(LOG_LABEL, "查询下载完成列表");
                                // setListContainer("下载完成");
                                FileDownloadItemView downloadedItemView = new FileDownloadItemView(abilitySlice, "下载完成");
                                fileDownloadItemProvider.setFileDownloadItemInfos(downloadedItemView.getFileDownloadItemInfos());
                                fileDownloadItemProvider.notifyDataChanged();
                                break;
                            case 1:
                                HiLog.debug(LOG_LABEL, "查询正在下载列表");
                                // setListContainer("正在下载");
                                FileDownloadItemView downloadingItemView = new FileDownloadItemView(abilitySlice, "正在下载");
                                fileDownloadItemProvider.setFileDownloadItemInfos(downloadingItemView.getFileDownloadItemInfos());
                                fileDownloadItemProvider.notifyDataChanged();
                                break;
                            default:
                                HiLog.warn(LOG_LABEL, "查询下载数据越界");
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
     * 获取文件下载数据
     */
    private void getDownloadData(){
        switch (tabListDownload.getSelectedTabIndex()){
            case 0:
                HiLog.debug(LOG_LABEL, "查询下载完成列表");
                setListContainer("下载完成");
                break;
            case 1:
                HiLog.debug(LOG_LABEL, "查询正在下载列表");
                setListContainer("正在下载");
                break;
            default:
                HiLog.warn(LOG_LABEL, "查询下载数据越界");
                break;
        }
    }

    /**
     * 设置 ListContainer
     * @param selectDownloadStatus
     */
    private void setListContainer(String selectDownloadStatus){
        FileDownloadItemView fileDownloadItemView = new FileDownloadItemView(abilitySlice, selectDownloadStatus);
        fileDownloadItemProvider = new FileDownloadItemProvider(fileDownloadItemView.getFileDownloadItemInfos());

        downloadFileContainer.setItemProvider(fileDownloadItemProvider);

        // 设置 ListContainer 的事件监听器
        setListClickListener();

        // 设置 ListContainer 回滚动画
        setListReboundAnimation();
    }

    /**
     * 设置 ListContainer 的事件监听器
     */
    private void setListClickListener(){
        // downloadFileContainer 子项单击事件
        downloadFileContainer.setItemClickedListener( (listContainer, component, position, id) -> {
            FileDownloadItemInfo fileDownloadItemInfo = (FileDownloadItemInfo) fileDownloadItemProvider.getItem(position);
            // Toast.makeToast(abilitySlice, fileDownloadItemInfo.toString(), Toast.TOAST_SHORT).show();
            // [TODO] 支持更多类型文件预览
            // 图像文件预览
            StringBuilder builder = new StringBuilder();
            builder.append(FILE_BASE_URI);
            builder.append(FILE_DATA_PATH);
            builder.append('?');
            builder.append(fileDownloadItemInfo.getLinkName());
            Intent intent = new Intent();
            intent.setParam("imageName", fileDownloadItemInfo.getLinkName());
            intent.setParam("imageUriStr", builder.toString());
            present(new ImagePreviewAbilitySlice(), intent);
        });
    }

    /**
     * 设置 ListContainer 回滚动画
     */
    private void setListReboundAnimation(){
        downloadFileContainer.setReboundEffect(true);
        downloadFileContainer.setReboundEffectParams(OVER_SCROLL_PERCENT, OVER_SCROLL_RATE, REMAIN_VISIBLE_PERCENT);
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
            if (position == 0){
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
            return;
        } else if (position == 1){
            present(new FileUploadAbilitySlice(), new Intent());
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
