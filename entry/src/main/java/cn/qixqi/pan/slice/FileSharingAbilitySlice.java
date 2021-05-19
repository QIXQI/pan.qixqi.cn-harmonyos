package cn.qixqi.pan.slice;

import cn.qixqi.pan.MyApplication;
import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.dao.TokenDao;
import cn.qixqi.pan.dao.impl.TokenDaoImpl;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;
import cn.qixqi.pan.datamodel.FileShareItemInfo;
import cn.qixqi.pan.model.FileShare;
import cn.qixqi.pan.util.ElementUtil;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.Toast;
import cn.qixqi.pan.view.BottomBarItemView;
import cn.qixqi.pan.view.FileShareItemView;
import cn.qixqi.pan.view.adapter.FileShareItemProvider;
import com.alibaba.fastjson.JSON;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class FileSharingAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileSharingAbilitySlice.class.getName());
    private static final String GET_FILE_SHARE_URL = "http://ali4.qixqi.cn:5555/api/filesharing/v1/filesharing/fileShare/user";

    // ListContainer 弹性回滚效果参数
    private static final int OVER_SCROLL_PERCENT = 40;
    private static final float OVER_SCROLL_RATE = 0.6f;
    private static final int REMAIN_VISIBLE_PERCENT = 20;

    private TokenDao tokenDao;
    private List<FileShare> fileShares;
    private AbilitySlice abilitySlice;

    private ListContainer fileSharesContainer;
    private FileShareItemProvider fileShareItemProvider;

    private Text title;
    private DirectionalLayout downloadItemLayout;
    private DirectionalLayout uploadItemLayout;

    private List<BottomBarItemInfo> bottomBarItemInfoList;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_file_sharing);

        abilitySlice = this;

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        tokenDao = new TokenDaoImpl(MyApplication.getAppContext());
        initView();
        initListener();

        // 设置底部导航栏
        setBottomToolBar();

        getFileShare();
    }

    /**
     * 初始化控件和布局
     */
    private void initView(){
        fileSharesContainer = (ListContainer) findComponentById(ResourceTable.Id_file_share_container);
        // **********标题栏*************
        title = (Text) findComponentById(ResourceTable.Id_title);
        title.setText(ResourceTable.String_share_title);
        downloadItemLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_download_item_layout);
        uploadItemLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_upload_item_layout);
    }

    /**
     * 初始化监听器
     */
    private void initListener(){
        // **********标题栏*************
        // 标题文本点击事件
        title.setClickedListener( component -> {
            Toast.makeToast(abilitySlice, "点击标题", Toast.TOAST_SHORT).show();
        });
        // downloadItemLayout 点击事件
        downloadItemLayout.setClickedListener(component -> {
            Toast.makeToast(abilitySlice, "点击downloadItemLayout", Toast.TOAST_SHORT).show();
        });
        // 点击 uploadItemLayout
        uploadItemLayout.setClickedListener( component -> {
            Toast.makeToast(abilitySlice, "点击uploadItemLayout", Toast.TOAST_SHORT).show();
        });
    }

    /**
     * 访问后端，获取用户文件分享列表
     */
    private void getFileShare(){
        String accessToken = tokenDao.get().getAccessToken();
        Map<String, String> addHeaders = new HashMap<>();
        String Authorization = String.format("Bearer %s", accessToken);
        addHeaders.put("Authorization", Authorization);
        HttpUtil.get(GET_FILE_SHARE_URL, null, addHeaders, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                HiLog.error(LOG_LABEL, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                if (response.isSuccessful()){
                    HiLog.debug(LOG_LABEL, responseStr);
                    fileShares = JSON.parseArray(responseStr, FileShare.class);
                    HiLog.debug(LOG_LABEL, fileShares.toString());

                    // 设置 ListContainer
                    // 在主线程(UI线程)中执行
                    TaskDispatcher uiTaskDispatcher = getUITaskDispatcher();
                    uiTaskDispatcher.asyncDispatch(() -> {
                        setListContainer();
                    });
                } else {
                    HiLog.error(LOG_LABEL, responseStr);
                }
            }
        });
    }

    /**
     * 获取文件分享列表后，设置 ListContainer
     */
    private void setListContainer(){
        if (fileShares == null){
            HiLog.warn(LOG_LABEL, "fileShares 为空");
            return;
        }
        FileShareItemView fileShareItemView = new FileShareItemView(fileShares);
        fileShareItemProvider = new FileShareItemProvider(fileShareItemView.getFileShareItemInfos());

        fileSharesContainer.setItemProvider(fileShareItemProvider);

        // 设置 ListContainer 的事件监听器
        setListClickListener();

        // 设置文件分享列表回滚动画
        setListReboundAnimation();
    }

    /**
     * 设置 ListContainer 的事件监听器
     */
    private void setListClickListener(){
        // fileSharesContainer 子项单击事件
        fileSharesContainer.setItemClickedListener( (listContainer, component, position, id) -> {
            FileShareItemInfo fileShareItemInfo = (FileShareItemInfo) fileShareItemProvider.getItem(position);
            Toast.makeToast(abilitySlice, fileShareItemInfo.toString(), Toast.TOAST_SHORT).show();
        });
    }

    /**
     * 设置文件列表回滚动画
     */
    private void setListReboundAnimation(){
        fileSharesContainer.setReboundEffect(true);
        fileSharesContainer.setReboundEffectParams(OVER_SCROLL_PERCENT, OVER_SCROLL_RATE, REMAIN_VISIBLE_PERCENT);
    }

    /**
     * 设置底部导航栏
     */
    private void setBottomToolBar(){
        BottomBarItemView bottomBarItemView = new BottomBarItemView();
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
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")
                    .withBundleName("cn.qixqi.pan")
                    .withAbilityName("cn.qixqi.pan.FileSystemAbility")
                    .build();
            intent.setOperation(operation);
            // 释放掉栈内所有的 Ability，不再返回先前页面
            intent.setFlags(Intent.FLAG_ABILITY_CLEAR_MISSION | Intent.FLAG_ABILITY_NEW_MISSION);
            startAbility(intent);
        } else if (position == 1){
            return;
        } else if (position == 2) {

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
